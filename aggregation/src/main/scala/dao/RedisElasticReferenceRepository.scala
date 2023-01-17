package ru.red.car_meta.aggregation
package dao

import config.RedisConfig

import cats.effect.kernel.Async
import cats.effect.{Resource, Sync}
import cats.implicits._
import com.redis.RedisClient
import com.redis.serialization.Parse
import ru.red.car_meta.elastic.domain.elastic.ElasticReference
import ru.red.car_meta.scraper.domain.car_domain.{CarAd, CarAdList}

sealed trait RedisRepository[F[_]] {
  def getByModel(model: String): F[Option[ElasticReference]]
  def cacheReference(reference: ElasticReference): F[Unit]

  def getCarAdvertisementDLQ(model: String): fs2.Stream[F, CarAd]
  def putCarAdvertisementInDLQ(carAd: CarAd): F[Unit]
}
object RedisRepository {
  def createClient[F[_] : Sync](cfg: RedisConfig): Resource[F, RedisClient] =
   Resource.fromAutoCloseable(Sync[F].delay(new RedisClient(cfg.host, cfg.port)))

  def apply[F[_]: RedisRepository]: RedisRepository[F] = implicitly[RedisRepository[F]]
}

class RedisElasticReferenceRepository[F[_]: Async](client: RedisClient, cfg: RedisConfig) extends RedisRepository[F] {


  implicit val carListParse: Parse[CarAdList] = Parse(CarAdList.parseFrom)
  implicit val elasticReferenceParse: Parse[ElasticReference] = Parse(ElasticReference.parseFrom)

  /*
   * Секция по работе с кешем идентификаторов Elasticsearch
   */

  override def getByModel(model: String): F[Option[ElasticReference]] =
    Async[F].delay(client.get[ElasticReference](model))

  override def cacheReference(reference: ElasticReference): F[Unit] =
    Async[F].delay(client.set(reference.model, reference.toByteArray))


  /*
   * Секция по работе с Dead Letter Queue уведомлений
   */

  override def getCarAdvertisementDLQ(model: String): fs2.Stream[F, CarAd] =
    fs2.Stream.eval(Async[F].delay(client.hget[CarAdList](cfg.carAdDlqName, model)
                                         .map(_.advertisements))
    ).filter(_.isDefined)
     .flatMap(adList => fs2.Stream.evalSeq(Async[F].pure(adList.get)))

  override def putCarAdvertisementInDLQ(carAd: CarAd): F[Unit] =
    Async[F].delay(client.hget[CarAdList](cfg.carAdDlqName, carAd.model)
      .map(_.addAdvertisements(carAd))
      .fold(client.hset(cfg.carAdDlqName, carAd.model, CarAdList(carAd :: Nil).toByteArray))
           (list => client.hset(cfg.carAdDlqName, carAd.model, list.toByteArray))
    )
    .void
}
