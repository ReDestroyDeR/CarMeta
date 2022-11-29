package ru.red.car_meta.aggregation

import cats.effect.{ExitCode, IO, IOApp}
import ru.red.car_meta.aggregation.elastic.ElasticImplicits

object AggregationApplication extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    ElasticImplicits.client[IO]
      .flatTap(IO.println)
      .map(_.close())
      .as(ExitCode.Success)
}
