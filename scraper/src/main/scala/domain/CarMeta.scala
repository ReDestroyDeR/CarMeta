package ru.red.car_meta.scraper
package domain

case class CarMeta(model: Model,
                   categoryDefinitionUrl: CarUrl,
                   pages: List[UncategorizedPage],
                   sourceSite: Source,
                   retrievedAt: RetrievedAt)
