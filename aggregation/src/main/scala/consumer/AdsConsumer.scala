package ru.red.car_meta.aggregation
package consumer

import config.KafkaConfig

import cats.effect.Async
import cats.implicits._
import fs2.kafka.{AutoOffsetReset, ConsumerSettings, Deserializer}
import ru.red.car_meta.scraper.domain.car_domain.CarAd

object AdsConsumer {
  implicit def deserializer[F[_]: Async]: Deserializer[F, CarAd] =
    Deserializer.lift[F, CarAd](
      bytes => Async[F].delay(CarAd.parseFrom(bytes))
    )

  def consumerSettings[F[_]: Async](cfg: KafkaConfig): ConsumerSettings[F, String, CarAd] =
    ConsumerSettings[F, String, CarAd]
      .withGroupId(cfg.groupId)
      .withBootstrapServers(cfg.bootstrapServers)
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withEnableAutoCommit(true)
}
