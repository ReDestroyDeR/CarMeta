package ru.red.car_meta.aggregation
package elastic

import config.ElasticConfig
import config.KafkaConfig._

import cats.effect.kernel.Async
import cats.implicits._
import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties}

object ElasticImplicits {
  implicit def client[F[_]: Async]: F[ElasticClient] = for {
    config <- ElasticConfig.load[F]
    properties <- Async[F].pure(ElasticProperties(config.host))
  } yield ElasticClient(JavaClient(properties))
}
