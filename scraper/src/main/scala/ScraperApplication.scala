package ru.red.car_meta.scraper

import domain.car_domain.{CarAd, CarDefinition, CarEntity}
import producer.CarDefinitionProducer._
import scraper.scrapers
import service.{HtmlScraper, ScrapingService}

import cats.effect.{ExitCode, IO, IOApp}
import fs2.kafka.{KafkaProducer, ProducerRecord, ProducerRecords}
import scalapb.GeneratedMessage

object ScraperApplication extends IOApp {
  implicit val scrapingService: HtmlScraper[IO] = new ScrapingService()

  override def run(args: List[String]): IO[ExitCode] = (for {
    settings <- producerSettings[IO]
  } yield fs2.Stream.evalSeq(IO.pure(scrapers))
    .flatMap(_.run[IO, ProducerRecord[String, CarEntity with GeneratedMessage]]{
      case definition @ CarDefinition(brand,_,_,_,_,_,_,_,_,_,_,_) =>
        ProducerRecord("car-definitions", brand, definition)
      case ad @ CarAd(brand,_,_,_,_,_) =>
        ProducerRecord("car-ads", brand, ad)
    })
    .map(record => ProducerRecords.one(record))
    .through(KafkaProducer.pipe(settings))
    .repeat.compile.drain) // Update data indefinitely
    .as(ExitCode.Success)


}
