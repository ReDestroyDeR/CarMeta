package ru.red.car_meta.scraper

import cats.data.Chain

package object scraper {
  val scrapers: Chain[Scraper] = Chain(AvtoRussiaScraper, AvitoScraper)
}
