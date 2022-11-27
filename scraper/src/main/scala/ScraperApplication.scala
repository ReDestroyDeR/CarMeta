package ru.red.car_meta.scraper

import domain.car_domain.{CarAd, CarDefinition, CarEntity}
import producer.CarDefinitionProducer._
import scraper.scrapers
import service.{HtmlScraper, ScrapingService}

import cats.effect.{ExitCode, IO, IOApp}
import fs2.kafka.{KafkaProducer, ProducerRecord, ProducerRecords}

object ScraperApplication extends IOApp {
  implicit val scrapingService: HtmlScraper[IO] = new ScrapingService()

  override def run(args: List[String]): IO[ExitCode] = (for {
    settings <- producerSettings[IO]
  } yield fs2.Stream.evalSeq(IO.pure(scrapers))
    .flatMap(_.run[IO, ProducerRecord[String, CarEntity]]{
      case definition @ CarDefinition(brand) =>
        ProducerRecord("car-definitions", brand, definition)
      case ad @ CarAd(brand) =>
        ProducerRecord("car-ads", brand, ad)
    })
    .map(ProducerRecords.one)
    .through(KafkaProducer.pipe(settings))
    .repeat) // Update data indefinitely
    .as(ExitCode.Success)


}
