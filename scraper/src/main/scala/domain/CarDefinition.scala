package ru.red.car_meta.scraper
package domain

case class CarDefinition(model: Model,
                         height: Meters,
                         width: Meters,
                         length: Meters,
                         maximumAllowedMass: Kilograms,
                         maximumSpeed: Speed,
                         accelerationSpeed: AccelerationSpeed,
                         seats: Seats,
                         sourceLink: Link,
                         sourceSite: Source,
                         retrievedAt: RetrievedAt)
