package ru.red.car_meta.scraper
package producer

import config.KafkaConfig
import domain.car_domain.CarEntity

import cats.effect.kernel.Async
import cats.implicits._
import fs2.kafka.{ProducerSettings, Serializer}
import scalapb.GeneratedMessage


object CarDefinitionProducer {
  def producerSettings[F[_]: Async](config: KafkaConfig): ProducerSettings[F, String, CarEntity with GeneratedMessage] =
    ProducerSettings(keySerializer = Serializer[F, String],
      valueSerializer = Serializer.lift[F, CarEntity with GeneratedMessage]
        (carEntity => Async[F].pure(carEntity.toByteArray))
    ).withBootstrapServers(config.bootstrapServers)
}