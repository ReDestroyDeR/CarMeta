package ru.red.car_meta.scraper
package domain

import scraper.Scraper

import cats.Monad

sealed trait Source {
  def getScraper[F[_]: Monad]: F[Scraper]
  def getDomain[F[_]: Monad]: F[String]
}
object Source {
  def apply(domain: String, scraper: Scraper): Source =
     new Source {
       override def getScraper[F[_]: Monad]: F[Scraper] = Monad[F].pure(scraper)

       override def getDomain[F[_]: Monad]: F[String] = Monad[F].pure(domain)
     }
}
