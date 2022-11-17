package ru.red.car_meta.scraper
package service

import domain.Link

import net.ruippeixotog.scalascraper.model.Document

trait CoreScraper[F[_]] {
  def parseUrl[T <: Link](url: T): F[Document]
}
object CoreScraper {
  def apply[F[_]: CoreScraper]: CoreScraper[F] = implicitly[CoreScraper[F]]
}
