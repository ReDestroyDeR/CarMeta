package ru.red.car_meta.aggregation
package elastic

import cats.effect.kernel.Async
import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties}
import ru.red.car_meta.aggregation.config.ElasticConfig

object ElasticImplicits {
  implicit def client[F[_]: Async]: F[ElasticClient] = for {
    config <- ElasticConfig.load[F]
    properties <- Async[F].pure(ElasticProperties(config.host))
  } yield ElasticClient(JavaClient(properties))
}

