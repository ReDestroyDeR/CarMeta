package ru.red.car_meta.scraper
package service

import domain.Link

import cats.effect.Sync
import cats.implicits._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Document

class ScrapingService[F[_]: Sync] extends CoreScraper[F] {
  private val browser = JsoupBrowser()

  def parseUrl[T <: Link](url: T): F[Document] =
    Sync[F].delay(browser.get(url.url))

}
