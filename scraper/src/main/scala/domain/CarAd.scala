package ru.red.car_meta.scraper
package domain

case class CarAd(model: Model,
                 height: Height,
                 width: Width,
                 maximumAllowedMass: MaximumAllowedMass,
                 maximumSpeed: MaximumSpeed,
                 accelerationSpeed: AccelerationSpeed,
                 seats: Seats,
                 price: Money,
                 adUrl: Link,
                 retrievedAt: RetrievedAt)


