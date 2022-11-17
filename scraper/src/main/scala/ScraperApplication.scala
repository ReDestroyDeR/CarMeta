package ru.red.car_meta.scraper

import cats.effect.{ExitCode, IO, IOApp}

object ScraperApplication extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    IO.println("Hello, World!").as(ExitCode.Success)
}
