package ru.red.car_meta.aggregation
package service

import dao.{ElasticRepository, RedisRepository}

import cats.Monad
import cats.effect.kernel.Concurrent
import cats.implicits._
import fs2.Stream
import org.typelevel.log4cats.Logger
import ru.red.car_meta.elastic.domain.elastic.ElasticReference
import ru.red.car_meta.scraper.domain.car_domain.{Car, CarAd, CarDefinition}


sealed trait ElasticProcessor[F[_]] {
  def processDefinitions(definitions: Stream[F, CarDefinition]): Stream[F, Unit]

  def processAds(ads: Stream[F, CarAd]): Stream[F, Unit]
}

object ElasticProcessor {
  def apply[F[_] : ElasticProcessor]: ElasticProcessor[F] = implicitly[ElasticProcessor[F]]
}

class ElasticProcessorImpl[F[_] : Monad : Concurrent : Logger : ElasticRepository : RedisRepository] extends ElasticProcessor[F] {
  private val parallelism: Int = Runtime.getRuntime.availableProcessors();

  override def processDefinitions(definitions: Stream[F, CarDefinition]): Stream[F, Unit] = {
    definitions.parEvalMapUnordered(parallelism) { definition =>
      for {
        elasticId <- RedisRepository[F].getByModel(definition.model)
      } yield Car(
        elasticId = elasticId.map(_.id),
        brand = definition.brand,
        model = definition.model,
        height = definition.height,
        width = definition.width,
        length = definition.length,
        maximumAllowedMass = definition.maximumAllowedMass,
        maximumSpeed = definition.maximumSpeed,
        accelerationSpeed = definition.accelerationSpeed,
        seats = definition.seats,
        sourceLink = definition.sourceLink,
        retrievedAt = definition.retrievedAt,
        advertisements = List()
      )
    }.parEvalMapUnordered(parallelism)(car =>
      RedisRepository[F].getByModel(car.model)
        .flatMap(_.fold {
          for {
            response <- ElasticRepository[F].put(car)
            carWithId = car.withElasticId(response.result.id)
            _ <- RedisRepository[F].cacheReference(ElasticReference(carWithId.model, carWithId.elasticId.get))
            _ <- Concurrent[F].start(
              processAds(RedisRepository[F].getCarAdvertisementDLQ(carWithId.model))
                .compile
                .drain
            )
          } yield ()
        } { eRef =>
          for {
            _ <- Logger[F].info(s"Pushing record to elastic by $eRef")
            response <- ElasticRepository[F].put(eRef.id, car.withElasticId(eRef.id))
          } yield ()
        })
    )
  }

  override def processAds(ads: Stream[F, CarAd]): Stream[F, Unit] =
    ads.evalMap { ad => (
      for {
        elasticId <- RedisRepository[F].getByModel(ad.model)
        _ <- Logger[F].info(s"Got elastic id for $ad - $elasticId")
      } yield elasticId.fold(RedisRepository[F].putCarAdvertisementInDLQ(ad)
      )(ref => ElasticRepository[F].put(ref.id, ad).void))
      .flatten
    }
}
