package ru.red.car_meta.aggregation
package consumer

import config.KafkaConfig

import cats.effect._
import cats.syntax.all._
import fs2.kafka._
import ru.red.car_meta.scraper.domain.car_domain.CarDefinition

import scala.concurrent.duration._

object DefinitionConsumer {
  implicit def deserializer[F[_]: Async]: Deserializer[F, CarDefinition] =
    Deserializer.lift[F, CarDefinition](
      bytes => Async[F].delay(CarDefinition.parseFrom(bytes))
    )

  implicit def consumerSettings[F[_]: Async]: F[ConsumerSettings[F, String, CarDefinition]] = for {
    cfg <- KafkaConfig.load[F]
  } yield ConsumerSettings[F, String, CarDefinition]
    .withBootstrapServers(cfg.bootstrapServers)
}
