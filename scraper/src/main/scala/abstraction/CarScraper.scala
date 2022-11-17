package ru.red.car_meta.scraper
package abstraction

import domain.{CarAd, CarMeta, CategoryDefinitionUrl}

trait CarScraper[F[_]] {
  def getCategories: fs2.Stream[F, CategoryDefinitionUrl]

  def getCarsMetasInCategory(categoryDefinitionUrl: CategoryDefinitionUrl): fs2.Stream[F, CarMeta]

  def parseCarMeta(carMeta: CarMeta): F[CarAd]
}
