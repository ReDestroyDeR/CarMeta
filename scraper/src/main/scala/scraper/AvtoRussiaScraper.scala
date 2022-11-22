package ru.red.car_meta.scraper
package scraper

import domain._
import service.HtmlScraper

import cats.Monad
import cats.data.OptionT
import cats.effect.kernel.{Clock, Concurrent}
import cats.implicits._
import fs2._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList

import scala.util.Try

class AvtoRussiaScraper extends CarDefinitionScraper {
  private val sitemaps: List[String] = List(
    "https://avto-russia.ru/sitemap1.xml",
    "https://avto-russia.ru/sitemap2.xml"
  )

  override def getDefinitionUrls[F[_]: Monad: Concurrent: HtmlScraper]: Stream[F, Link] =
    Stream.iterable(sitemaps)
      .evalMap(HtmlScraper[F].parseUrl(_))
      .map(xml =>
        s"""
           |<head></head>
           |<body>$xml</body>
           |""".stripMargin
      )
      .evalMap(HtmlScraper[F].parseString(_))
      .filter(b => b.body != null)
      .flatMap(sitemap => Stream.iterable(sitemap >> elementList("loc")))
      .map(_.text)
      .filter(link => link.endsWith(".html"))
      .filter(link => link.contains("autos") || link.contains("moto"))
      .map(Link)

  override def parseCarLink[F[_]: Monad: Concurrent: HtmlScraper](carLink: Link): F[Option[CarDefinition]] = for {
    // Get HTML
    document <- OptionT.liftF(HtmlScraper[F].parseUrl(carLink.url))
    // Fail fast if header is not present
    header <- OptionT(Monad[F].point(document)
      .map(doc => Option(doc >> allText("h1"))
        .filter(text => text.contains("Технические характеристики"))))
    // Build elements map
    elements <- OptionT.liftF(Stream.evalSeq(Monad[F].point(document >> elementList("table")))
      .flatMap(table => Stream.evalSeq(Monad[F].point(table >> elementList("tr"))))
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
    // Extract optional car data
    model <- OptionT(Monad[F].point(elements.get("Модификация")))
    height <- OptionT(Monad[F].point(elements.get("Высота"))).map(parseDouble)
    width <- OptionT(Monad[F].point(elements.get("Ширина"))).map(parseDouble)
    length <- OptionT(Monad[F].point(elements.get("Длина"))).map(parseDouble)
    maximumAllowedMass <- OptionT(Monad[F].point(elements.get("Полная масса"))).map(parseDouble)
    maximumSpeed <- OptionT(Monad[F].point(elements.get("Максимальная скорость"))).map(parseDouble)
    accelerationSpeed <- OptionT(Monad[F].point(elements.get("Время разгона до 100 км/ч"))).map(parseDouble)
    seats <- OptionT(Monad[F].point(elements.get("Количество мест"))).map(_.toInt)
    retrievedAt <- OptionT.liftF(Clock[F].realTime.map(RetrievedAt))
    domain <- OptionT.liftF(getDomain)
  } yield CarDefinition(
      Model(model),
      Meters(height),
      Meters(width),
      Meters(length),
      Kilograms(maximumAllowedMass),
      Speed(maximumSpeed),
      AccelerationSpeed(accelerationSpeed),
      Seats(seats),
      carLink,
      domain,
      retrievedAt
  )

  override def getDomain: F[Source[F]] = Monad[F].pure("https://avto-russia.ru")

  private def parseDouble(str: String): Double =
    Try(str.split(" ")(0).toDouble).getOrElse(0.0d)
}
