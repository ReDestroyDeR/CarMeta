package ru.red.car_meta.scraper
package scraper

import domain.{CarAd, CarUrl}

trait CarScraper[F[_]] {
  def getCarLinks: fs2.Stream[F, CarUrl]

  def parseCarLink(carLink: CarUrl): F[CarAd]
}
