package ru.red.car_meta.scraper
package scraper

import domain.Source
import service.HtmlScraper

import cats.Monad
import cats.effect.kernel.{Clock, Concurrent}
import ru.red.car_meta.scraper.domain.car_domain.{CarAd, CarDefinition}

sealed trait Scraper {
  def getDomain[F[_]: Monad]: F[Source]
}

trait CarAdScraper extends Scraper {
  def getAdUrls[F[_]: Monad: Concurrent: HtmlScraper]: fs2.Stream[F, String]

  def parseAdLink[F[_]: Monad: Concurrent: Clock: HtmlScraper](carLink: String): F[Option[CarAd]]
}

trait CarDefinitionScraper extends Scraper {
  def getDefinitionUrls[F[_]: Monad: Concurrent: HtmlScraper]: fs2.Stream[F, String]

  def parseCarLink[F[_]: Monad: Concurrent: Clock: HtmlScraper](carLink: String): F[Option[CarDefinition]]
}

