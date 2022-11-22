package ru.red.car_meta.scraper
package domain

import scraper.Scraper

import cats.Monad

sealed trait Source {
  def getScraper[F[_]: Monad]: F[Scraper[F]]
  def getDomain[F[_]: Monad]: F[Link]
}
object Source {
  def apply[G[_]: Monad](domain: Link, scraper: Scraper[G]): Source =
     new Source {
       override def getScraper[F[_]: Monad]: F[Scraper[F]] = Monad[F].pure(domain)

       override def getDomain[F[_]: Monad]: F[Link] = Monad[F].pure(scraper)
     }
}
