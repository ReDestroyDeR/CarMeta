package ru.red.car_meta.aggregation
package dao

import config.ElasticConfig
import dao.ElasticRepository.client
import domain.{CarHitReader, CarIndexable}
import elastic4s._

import cats.data.OptionT
import cats.effect.kernel.Async
import cats.implicits._
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.requests.indexes.IndexResponse
import com.sksamuel.elastic4s.requests.update.UpdateResponse
import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties, Response}
import ru.red.car_meta.scraper.domain.car_domain.{Car, CarAd}

sealed trait ElasticRepository[F[_]] {
  def put(car: Car): F[Response[IndexResponse]]

  def put(id: String, car: Car): F[Response[UpdateResponse]]

  def put(id: String, ad: CarAd): F[Response[UpdateResponse]]
}

object ElasticRepository {
  implicit def client[F[_] : Async]: F[ElasticClient] = for {
    config <- ElasticConfig.load[F]
    properties <- Async[F].pure(ElasticProperties(config.host))
  } yield ElasticClient(JavaClient(properties))

  def apply[F[_] : ElasticRepository]: ElasticRepository[F] = implicitly[ElasticRepository[F]]
}

class ElasticRepositoryImpl[F[_] : Async] extends ElasticRepository[F] {
  override def put(car: Car): F[Response[IndexResponse]] = for {
    config <- ElasticConfig.load[F]
    client <- client[F]
    res <- client.execute {
      indexInto(config.carIndexName).doc(car)
    }
  } yield res

  override def put(id: String, car: Car): F[Response[UpdateResponse]] = for {
    config <- ElasticConfig.load[F]
    client <- client[F]
    update <- client.execute {
      updateById(config.carIndexName, id).doc(car)
    }
  } yield update

  override def put(id: String, ad: CarAd): F[Response[UpdateResponse]] = (for {
    config <- OptionT.liftF(ElasticConfig.load[F])
    client <- OptionT.liftF(client[F])
    car <- OptionT(client.execute(search(config.carIndexName).query(idsQuery(id)))
      .map(_.result.to[Car].headOption))
    update <- OptionT.liftF(client.execute {
      val newCar = car.withAdvertisements(ad :: car.advertisements.toList)
      updateById(config.carIndexName, id).doc(newCar)
    })
  } yield update).getOrRaise(new NoSuchElementException(s"No car ad found by $id"))
}
