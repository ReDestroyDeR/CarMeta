package ru.red.car_meta.scraper
package service

import cats.effect.Sync
import cats.effect.implicits._
import cats.implicits._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupDocument
import net.ruippeixotog.scalascraper.model.Document

import scala.util.Try

class ScrapingService[F[_]: Sync] extends HtmlScraper[F] {
  private val browser = JsoupBrowser()

  override def parseUrl(url: String): F[Document] =
    Sync[F].delay(Try(browser.get(url))
      .getOrElse(JsoupDocument(org.jsoup.nodes.Document.createShell("not found"))))


  override def parseString(html: String): F[Document] =
    Sync[F].delay(browser.parseString(html))
}
