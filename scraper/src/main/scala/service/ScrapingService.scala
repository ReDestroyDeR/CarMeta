package ru.red.car_meta.scraper
package service

import cats.effect.Sync
import cats.effect.implicits._
import cats.implicits._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupDocument
import net.ruippeixotog.scalascraper.model.Document
import org.jsoup.nodes

import scala.util.Try

class ScrapingService[F[_]: Sync](browser: JsoupBrowser) extends HtmlScraper[F] {

  override def parseUrl(url: String): F[Document] =
    Sync[F].delay(Try(browser.get(url))
      .getOrElse(JsoupDocument(nodes.Document.createShell("not found"))))


  override def parseString(html: String): F[Document] =
    Sync[F].delay(browser.parseString(html))
}
