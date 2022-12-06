package ru.red.car_meta.aggregation

import consumer.{AdsConsumer, DefinitionConsumer}
import dao.{ElasticRepository, ElasticRepositoryImpl, RedisElasticReferenceRepository, RedisRepository}
import service.{ElasticProcessor, ElasticProcessorImpl}

import cats.effect.implicits._
import cats.effect.{Concurrent, ExitCode, IO, IOApp}
import cats.implicits._
import fs2.kafka.KafkaConsumer
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object AggregationApplication extends IOApp {
  implicit val slf4jLogger: Logger[IO] = Slf4jLogger.getLogger[IO]
  implicit val elasticRepo: ElasticRepository[IO] = new ElasticRepositoryImpl[IO]
  implicit val redisRepo: RedisRepository[IO] = new RedisElasticReferenceRepository[IO]
  implicit val elasticProcessor: ElasticProcessor[IO] = new ElasticProcessorImpl[IO]

  override def run(args: List[String]): IO[ExitCode] =
    for {
      defs <- Concurrent[IO].start(fs2.Stream.eval(DefinitionConsumer.consumerSettings[IO])
        .flatMap(KafkaConsumer.stream(_))
        .subscribeTo("car-definitions")
        .records
        .map(_.record.value)
        .through(ElasticProcessor[IO].processDefinitions(_))
        .compile
        .drain)
      ads <- Concurrent[IO].start(fs2.Stream.eval(AdsConsumer.consumerSettings[IO])
        .flatMap(KafkaConsumer.stream(_))
        .subscribeTo("car-ads")
        .records
        .map(_.record.value)
        .through(ElasticProcessor[IO].processAds(_))
        .compile
        .drain)
      // Never
      _ <- defs.join
      _ <- ads.join
    } yield ExitCode.Success


}
