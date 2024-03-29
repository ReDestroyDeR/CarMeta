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

  def consumerSettings[F[_]: Async](cfg: KafkaConfig): ConsumerSettings[F, String, CarDefinition] =
    ConsumerSettings[F, String, CarDefinition]
      .withGroupId(cfg.groupId)
      .withBootstrapServers(cfg.bootstrapServers)
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withEnableAutoCommit(true)
}
