package ru.red.car_meta.scraper

import producer.CarDefinitionProducer._
import scraper.AvtoRussiaScraper
import service.{HtmlScraper, ScrapingService}

import cats.effect.{ExitCode, IO, IOApp}
import fs2.kafka.{KafkaProducer, ProducerRecord, ProducerRecords}

object ScraperApplication extends IOApp {
  implicit val scrapingService: HtmlScraper[IO] = new ScrapingService()

  override def run(args: List[String]): IO[ExitCode] = (for {
    settings <- producerSettings[IO]
  } yield AvtoRussiaScraper.getDefinitionUrls[IO]
    .drop(10000)
    .take(100)
    .evalMap(url => AvtoRussiaScraper.parseCarLink[IO](url))
    .filter(_.isDefined)
    .map(_.get)
    .map(definition => ProducerRecords.one(ProducerRecord("car-definitions-test", definition.brand, definition)))
    .through(KafkaProducer.pipe(settings))
    .compile.drain)
    .flatMap(a => a)
    .as(ExitCode.Success)


}
