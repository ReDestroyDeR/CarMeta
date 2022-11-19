package ru.red.car_meta.scraper

import scala.concurrent.duration.FiniteDuration

package object domain {
  case class Meters(value: Double) extends AnyVal
  case class Height(value: Meters)
  case class Width(value: Meters)

  case class Kilograms(value: Double) extends AnyVal
  case class MaximumAllowedMass(value: Kilograms)

  case class KilometersPerHour(value: Double) extends AnyVal
  case class MaximumSpeed(value: KilometersPerHour)
  case class AccelerationSpeed(value: KilometersPerHour)

  case class Seats(value: Int) extends AnyVal

  case class Currency(value: String) extends AnyVal
  case class Money(amount: Double, currency: Currency) {
    def ?=(other: Money): Boolean =
      currency == other.currency

    def +(other: Money): Option[Money] = {
      if (this ?= other)
        Some(Money(amount + other.amount, currency))
      None
    }

    def -(other: Money): Option[Money] = {
      if (this ?= other)
        Some(Money(amount - other.amount, currency))
      None
    }
  }

  case class Model(value: String) extends AnyVal

  case class Link(url: String) extends AnyVal
  case class CarUrl(url: Link)
  case class UncategorizedPage(url: Link)

  case class RetrievedAt(timestamp: FiniteDuration)
}