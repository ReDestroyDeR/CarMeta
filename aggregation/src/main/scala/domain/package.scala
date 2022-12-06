package ru.red.car_meta.aggregation

import com.google.protobuf.ByteString
import com.sksamuel.elastic4s.{Hit, HitReader, Indexable}
import io.circe.generic.encoding._
import io.circe.generic.semiauto._
import io.circe.{Decoder, DecodingFailure, Encoder, Json}
import ru.red.car_meta.scraper.domain.car_domain.{Car, CarAd, Currency, Money}
import scalapb.UnknownFieldSet

import scala.util.Try

package object domain {
  implicit object CarIndexable extends Indexable[Car] {
    implicit val byteStringEncoder: Encoder[ByteString] = Encoder(bytes => Json.fromString(bytes.toStringUtf8))
    implicit val unknownFieldSetFieldEncoder: Encoder[UnknownFieldSet.Field] = deriveEncoder[UnknownFieldSet.Field]
    implicit val unknownFieldSetEncoder: Encoder[UnknownFieldSet] = deriveEncoder[UnknownFieldSet]
    implicit val currencyEncoder: Encoder[Currency] = deriveEncoder[Currency]
    implicit val moneyEncoder: Encoder[Money] = deriveEncoder[Money]
    implicit val adEncoder: Encoder[CarAd] = deriveEncoder[CarAd]
    implicit val encoder: Encoder[Car] = deriveEncoder[Car]

    override def json(car: Car): String = encoder(car).noSpaces
  }

  implicit object CarHitReader extends HitReader[Car] {
    implicit val byteStringDecoder: Decoder[ByteString] = Decoder(c =>
      c.value.asString
        .map(ByteString.copyFromUtf8)
        .toRight(DecodingFailure("a", c.history))
    )
    implicit val unknownFieldSetFieldDecoder: Decoder[UnknownFieldSet.Field] = deriveDecoder[UnknownFieldSet.Field]
    implicit val unknownFieldSetDecoder: Decoder[UnknownFieldSet] = deriveDecoder[UnknownFieldSet]
    implicit val currencyDecoder: Decoder[Currency] = deriveDecoder[Currency]
    implicit val moneyDecoder: Decoder[Money] = deriveDecoder[Money]
    implicit val adDecoder: Decoder[CarAd] = deriveDecoder[CarAd]
    implicit val decoder: Decoder[Car] = deriveDecoder[Car]

    override def read(hit: Hit): Try[Car] =
      decoder(Json.fromString(hit.sourceAsString).hcursor).toTry
  }
}
