package ru.red.car_meta.scraper
package scraper

import domain.{CarAd, CarDefinition, Link, Source}

import cats.Monad
import cats.effect.kernel.Concurrent
import ru.red.car_meta.scraper.service.HtmlScraper

sealed trait Scraper {
  def getDomain[F[_]]: F[Source]
}

trait CarAdScraper extends Scraper {
  def getAdUrls[F[_]: Monad: Concurrent: HtmlScraper]: fs2.Stream[F, Link]

  def parseAdLink[F[_]: Monad: Concurrent: HtmlScraper](carLink: Link): F[Option[CarAd]]
}

trait CarDefinitionScraper extends Scraper {
  def getDefinitionUrls[F[_]: Monad: Concurrent: HtmlScraper]: fs2.Stream[F, Link]

  def parseCarLink[F[_]: Monad: Concurrent: HtmlScraper](carLink: Link): F[Option[CarDefinition]]
}

