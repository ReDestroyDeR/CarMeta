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

object AvitoScraper extends CarAdScraper {
  override def getAdUrls[F[_] : Monad : Concurrent : HtmlScraper]: Stream[F, String] =
    Stream.empty

  override def parseAdLink[F[_] : Monad : Concurrent : Clock : HtmlScraper](carLink: String): F[Option[CarAd]] =
    Monad[F].pure(Option.empty)

  override def getDomain[F[_]: Monad]: F[Source] = Monad[F].pure(Source("https://avito.ru", this))
}
