package ru.red.car_meta.scraper
package scraper

import domain._
import domain.car_domain.CarAd
import service.HtmlScraper

import cats.Monad
import cats.effect.kernel.{Clock, Concurrent}
import cats.implicits._
import fs2._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._

import scala.util.Try

object AvitoScraper extends CarAdScraper {
  override def getAdUrls[F[_] : Monad : Concurrent : HtmlScraper]: Stream[F, String] = ???

  override def parseAdLink[F[_] : Monad : Concurrent : Clock : HtmlScraper](carLink: String): F[Option[CarAd]] = ???

  override def getDomain[F[_]: Monad]: F[Source] = Monad[F].pure(Source("https://avto-russia.ru", this))

  private def parseDouble(str: String): Double =
    Try(str.split(" ")(0).toDouble).getOrElse(0.0d)
}
