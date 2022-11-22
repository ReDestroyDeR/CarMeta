package ru.red.car_meta.scraper

import cats.effect.{ExitCode, IO, IOApp}
import ru.red.car_meta.scraper.scraper.{AvtoRussiaScraper, CarDefinitionScraper}
import ru.red.car_meta.scraper.service.{HtmlScraper, ScrapingService}

object ScraperApplication extends IOApp {
  implicit val scrapingService: HtmlScraper[IO] = new ScrapingService()
  implicit val carScraper: CarDefinitionScraper = new AvtoRussiaScraper()

  override def run(args: List[String]): IO[ExitCode] =
    IO.delay(carScraper.getDefinitionUrls[IO])
      .map(links => links.drop(10000).parEvalMap(Runtime.getRuntime.availableProcessors())(carScraper.parseCarLink).take(50))
      .flatMap(links => links.compile.toList)
      .flatMap(IO.println(_))
      .as(ExitCode.Success)
}
