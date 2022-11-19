package ru.red.car_meta.scraper
package scraper
import domain._
import service.HtmlScraper

import cats.Monad
import cats.data.OptionT
import cats.effect.implicits._
import cats.effect.kernel.{Clock, Concurrent}
import cats.implicits._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList

class AvtoRussiaScraper [F[_]: Monad: HtmlScraper: Clock: Concurrent] extends CarScraper[F] {
  private val sitemaps: List[Link] = List(
    "https://avto-russia.ru/sitemap1.xml",
    "https://avto-russia.ru/sitemap2.xml")
    .map(Link)


  override def getCarLinks: fs2.Stream[F, CarUrl] =
    fs2.Stream.iterable(sitemaps)
      .evalMap(HtmlScraper[F].parseUrl(_))
      .flatMap(sitemap => fs2.Stream.iterable(sitemap >> elementList("id")))
      .map(_.text)
      .filter(link => link.endsWith(".html"))
      .filter(link => link.contains("autos") || link.contains("moto"))
      .map(Link)
      .map(CarUrl)

  override def parseCarLink(carLink: CarUrl): F[CarAd] = (for {
    document <- OptionT.liftF(HtmlScraper[F].parseUrl(carLink.url))
    elements <- OptionT.liftF(fs2.Stream.evalSeq(Monad[F].point(document >> elementList("table")))
      .flatMap(table => fs2.Stream.evalSeq(Monad[F].point(table >> elementList("tr"))))
      .map(_.children)
      .map(children => children.headOption
        .flatMap(title => children.tail.headOption
          .map(value => (title.text, value.text))
        )
      )
      .filter(_.isDefined)
      .map(_.get)
      .compile
      .fold(Map.empty[String, String])((map, nodes) => map + nodes))
    model <- OptionT(Monad[F].point(elements.get("Модификация")))
    height <- OptionT(Monad[F].point(elements.get("Высота"))).map(_.toDouble)
    width <- OptionT(Monad[F].point(elements.get("Ширина"))).map(_.toDouble)
    maximumAllowedMass <- OptionT(Monad[F].point(elements.get("Полная масса"))).map(_.toDouble)
    maximumSpeed <- OptionT(Monad[F].point(elements.get("Максимальная скорость"))).map(_.toDouble)
    accelerationSpeed <- OptionT(Monad[F].point(elements.get("Время разгона до 100 км/ч"))).map(_.toDouble)
    seats <- OptionT(Monad[F].point(elements.get("Количество мест"))).map(_.toInt)
    price <- OptionT.liftF(Monad[F].point("-")).map(_.toDouble)
    retrievedAt <- OptionT.liftF(Clock[F].realTime.map(RetrievedAt))
  } yield CarAd(
    Model(model),
    Height(Meters(height)),
    Width(Meters(width)),
    MaximumAllowedMass(Kilograms(maximumAllowedMass)),
    MaximumSpeed(KilometersPerHour(maximumSpeed)),
    AccelerationSpeed(KilometersPerHour(accelerationSpeed)),
    Seats(seats),
    Money(price, Currency("RUB")),
    carLink.url,
    retrievedAt)).getOrRaise(new UnknownError())
}
