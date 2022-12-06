package ru.red.car_meta.aggregation
package init

import config.ElasticConfig
import dao.ElasticRepository

import cats.effect.kernel.Async
import cats.implicits._
import com.sksamuel.elastic4s.ElasticApi.createIndex
import com.sksamuel.elastic4s.ElasticDsl.CreateIndexHandler

class ElasticInitializer[F[_]: Async] {
  def init: F[Unit] = for {
    cfg <- ElasticConfig.load[F]
    client <- ElasticRepository.client[F]
  } yield client.execute{
    createIndex(cfg.carIndexName)
  }
}
