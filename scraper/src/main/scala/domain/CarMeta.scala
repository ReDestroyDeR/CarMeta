package ru.red.car_meta.scraper
package domain

case class CarMeta(model: Model,
                   categoryDefinitionUrl: CategoryDefinitionUrl,
                   pages: List[UncategorizedPage],
                   sourceSite: Source,
                   retrievedAt: RetrievedAt)
