package ru.red.car_meta.scraper
package service

import cats.effect.Sync
import cats.implicits._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Document

class ScrapingService[F[_]: Sync] extends HtmlScraper[F] {
  private val browser = JsoupBrowser()

  override def parseUrl(url: String): F[Document] =
    Sync[F].delay(browser.get(url))

  override def parseString(html: String): F[Document] =
    Sync[F].delay(browser.parseString(html))
}
