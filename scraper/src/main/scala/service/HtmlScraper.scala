package ru.red.car_meta.scraper
package service

import net.ruippeixotog.scalascraper.model.Document

trait HtmlScraper[F[_]] {
  def parseUrl(url: String): F[Document]
  def parseString(html: String): F[Document]
}
object HtmlScraper {
  def apply[F[_]: HtmlScraper]: HtmlScraper[F] = implicitly[HtmlScraper[F]]
}
