package ru.red.car_meta.scraper

import cats.Monad

package object scraper {
  implicit def avito[F[_]: Monad]: AvitoScraper[F] = new AvitoScraper[F]
  implicit
}
