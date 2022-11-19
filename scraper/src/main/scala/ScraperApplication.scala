package ru.red.car_meta.scraper

import cats.effect.{ExitCode, IO, IOApp}
import ru.red.car_meta.scraper.scraper.{AvtoRussiaScraper, CarScraper}
import ru.red.car_meta.scraper.service.{HtmlScraper, ScrapingService}

object ScraperApplication extends IOApp {
  implicit val scrapingService: HtmlScraper[IO] = new ScrapingService()
  implicit val carScraper: CarScraper[IO] = new AvtoRussiaScraper()

  override def run(args: List[String]): IO[ExitCode] =
    IO.delay(carScraper.getCarLinks)
      .flatMap(links => links.foreach(IO.println(_)).compile.drain)
      .as(ExitCode.Success)
}
