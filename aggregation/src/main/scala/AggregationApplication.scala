package ru.red.car_meta.aggregation

import cats.effect.{ExitCode, IO, IOApp}

object AggregationApplication extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    IO.println("Hello from Aggregation!")
      .as(ExitCode.Success)
}
