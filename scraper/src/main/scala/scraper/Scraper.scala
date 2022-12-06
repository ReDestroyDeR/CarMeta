package ru.red.car_meta.scraper
package scraper

import domain.Source
import domain.car_domain.{CarAd, CarDefinition, CarEntity}
import service.HtmlScraper

import cats.Monad
import cats.effect.kernel.{Async, Clock, Concurrent}

sealed trait Scraper {
  def getDomain[F[_]: Monad]: F[Source]

  def run[F[_]: Async: HtmlScraper, B](partialFunction: PartialFunction[CarEntity, _ <: B]): fs2.Stream[F, B]
}

trait CarAdScraper extends Scraper {
  def getAdUrls[F[_]: Monad: Concurrent: HtmlScraper]: fs2.Stream[F, String]

  def parseAdLink[F[_]: Monad: Concurrent: Clock: HtmlScraper](carLink: String): F[Option[CarAd]]

  override def run[F[_]: Async: HtmlScraper, B](partialFunction: PartialFunction[CarEntity, _ <: B]): fs2.Stream[F, B] =
    getAdUrls[F]
      .parEvalMap(Runtime.getRuntime.availableProcessors())(parseAdLink[F])
      .filter(_.isDefined)
      .map(_.get)
      .filter(partialFunction.isDefinedAt)
      .map(partialFunction(_))
}

trait CarDefinitionScraper extends Scraper {
  def getDefinitionUrls[F[_] : Monad : Concurrent : HtmlScraper]: fs2.Stream[F, String]

  def parseCarLink[F[_] : Monad : Concurrent : Clock : HtmlScraper](carLink: String): F[Option[CarDefinition]]

  override def run[F[_] : Async : HtmlScraper, B](partialFunction: PartialFunction[CarEntity, _ <: B]): fs2.Stream[F, B] =
    getDefinitionUrls[F]
      .drop(10000)
      .parEvalMap(Runtime.getRuntime.availableProcessors())(parseCarLink[F])
      .filter(_.isDefined)
      .map(_.get)
      .filter(partialFunction.isDefinedAt)
      .map(partialFunction(_))
}

