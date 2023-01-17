package ru.red.car_meta.scraper

import config.KafkaConfig
import domain.car_domain.{CarAd, CarDefinition, CarEntity}
import producer.CarDefinitionProducer
import producer.CarDefinitionProducer._
import scraper.scrapers
import service.{HtmlScraper, ScrapingService}

import cats.effect.kernel.Async
import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits._
import fs2.kafka.{KafkaProducer, ProducerRecord, ProducerRecords}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import scalapb.GeneratedMessage

object ScraperApplication extends IOApp {
  implicit val scrapingService: HtmlScraper[IO] = new ScrapingService(new JsoupBrowser())

  def buildPipeline[F[_]: Async: HtmlScraper]: Resource[F, Unit] = for {
    kafkaConfig <- KafkaConfig.load[F]
    producerSettings <- Resource.pure(CarDefinitionProducer.producerSettings[F](kafkaConfig))
    _ <- Resource.eval(fs2.Stream.evalSeq(Async[F].pure(scrapers))
      .flatMap(_.run[F, ProducerRecord[String, CarEntity with GeneratedMessage]]{
        case definition @ CarDefinition(brand,_,_,_,_,_,_,_,_,_,_,_) => {
          ProducerRecord("car-definitions", brand, definition)
        }
        case ad @ CarAd(brand,_,_,_,_,_) =>
          ProducerRecord("car-ads", brand, ad)
      })
      .map(record => ProducerRecords.one(record))
      .through(KafkaProducer.pipe(producerSettings))
      .compile
      .drain)
  } yield ()

  override def run(args: List[String]): IO[ExitCode] =
    buildPipeline[IO].useForever.as(ExitCode.Success)


}
