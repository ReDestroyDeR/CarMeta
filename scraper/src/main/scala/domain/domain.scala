package ru.red.car_meta.scraper

import scala.concurrent.duration.FiniteDuration

package object domain {
  case class Meters(value: Double) extends AnyVal

  case class Kilograms(value: Double) extends AnyVal

  case class Speed(value: Double) extends AnyVal
  case class AccelerationSpeed(value: Double) extends AnyVal

  case class Seats(value: Int) extends AnyVal

  sealed trait Currency
  final case class RUB() extends Currency
  final case class USD() extends Currency
  final case class EUR() extends Currency

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

  case class RetrievedAt(timestamp: FiniteDuration) extends AnyVal
}