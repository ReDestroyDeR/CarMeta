package ru.red.car_meta.scraper
package producer

import config.KafkaConfig
import domain.car_domain.CarDefinition

import cats.effect.kernel.Async
import cats.implicits._
import fs2.kafka.{ProducerSettings, Serializer}


object CarDefinitionProducer {
  implicit def producerSettings[F[_]: Async]: F[ProducerSettings[F, String, CarDefinition]] = for {
    config <- KafkaConfig.load[F]
  } yield ProducerSettings(
    keySerializer = Serializer[F, String],
    valueSerializer = Serializer.lift[F, CarDefinition]
      (carDefinition => Async[F].pure(carDefinition.toByteArray))
  ).withBootstrapServers(config.bootstrapServers)
}