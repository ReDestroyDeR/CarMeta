package ru.red.car_meta.aggregation

import config.{ElasticConfig, KafkaConfig, RedisConfig}
import consumer.{AdsConsumer, DefinitionConsumer}
import dao.{ElasticRepository, ElasticRepositoryImpl, RedisElasticReferenceRepository, RedisRepository}
import service.ElasticProcessorImpl

import cats.effect.implicits._
import cats.effect.kernel.Async
import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits._
import fs2.kafka.KafkaConsumer
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object AggregationApplication extends IOApp {
  implicit val slf4jLogger: Logger[IO] = Slf4jLogger.getLogger[IO]

  def buildPipeline[F[_]: Async: Logger]: Resource[F, Unit] =
    for {
      elasticConfig <- ElasticConfig.load[F]
      redisConfig <- RedisConfig.load[F]
      kafkaConfig <- KafkaConfig.load[F]
      elasticClient <- ElasticRepository.createClient[F](elasticConfig)
      redisClient <- RedisRepository.createClient[F](redisConfig)
      elasticRepo <- Resource.eval(Async[F].delay(new ElasticRepositoryImpl[F](elasticClient, elasticConfig)))
      redisRepo <- Resource.eval(Async[F].delay(new RedisElasticReferenceRepository[F](redisClient, redisConfig)))
      elasticProcessor <- Resource.eval(Async[F].delay(new ElasticProcessorImpl[F](elasticRepo, redisRepo)))
      definitionConsumerSettings <- Resource.pure(DefinitionConsumer.consumerSettings[F](kafkaConfig))
      adsConsumerSettings <- Resource.pure(AdsConsumer.consumerSettings[F](kafkaConfig))
      defs <- Resource.eval(KafkaConsumer.stream(definitionConsumerSettings)
        .subscribeTo("car-definitions")
        .records
        .map(_.record.value)
        .through(elasticProcessor.processDefinitions)
        .compile
        .drain)
      ads <- Resource.eval(KafkaConsumer.stream(adsConsumerSettings)
        .subscribeTo("car-ads")
        .records
        .map(_.record.value)
        .through(elasticProcessor.processAds)
        .compile
        .drain)
    } yield ()

  override def run(args: List[String]): IO[ExitCode] =
    buildPipeline[IO].useForever.as(ExitCode.Success)

}
