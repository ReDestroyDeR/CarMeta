package ru.red.car_meta.scraper
package domain

import scraper.CarScraper

trait Source {
  def getScraper[F[_]]: CarScraper[F]
  def getDomain[F[_]]: F[String]
}
