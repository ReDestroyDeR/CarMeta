package ru.red.car_meta.scraper
package domain

case class CarAd(model: Model,
                 price: Money,
                 adUrl: Link,
                 source: Source,
                 retrievedAt: RetrievedAt)


