package ru.red.car_meta.scraper
package service

import domain.Link

import net.ruippeixotog.scalascraper.model.Document

trait HtmlScraper[F[_]] {
  def parseUrl[T <: Link](url: T): F[Document]
}
object HtmlScraper {
  def apply[F[_]: HtmlScraper]: HtmlScraper[F] = implicitly[HtmlScraper[F]]
}
