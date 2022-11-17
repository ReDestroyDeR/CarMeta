package ru.red.car_meta.scraper

package object domain {
  case class Meters(value: Double) extends AnyVal
  case class Height(value: Meters) extends AnyVal
  case class Width(value: Meters) extends AnyVal

  case class Kilograms(value: Double) extends AnyVal
  case class MaximumAllowedMass(value: Kilograms) extends AnyVal

  case class KilometersPerHour(value: Double) extends AnyVal
  case class MaximumSpeed(value: KilometersPerHour) extends AnyVal
  case class AccelerationSpeed(value: KilometersPerHour) extends AnyVal

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
  case class CategoryDefinitionUrl(url: Link) extends AnyVal
  case class UncategorizedPage(url: Link) extends AnyVal

  case class RetrievedAt(timestamp: Long) extends AnyVal
}