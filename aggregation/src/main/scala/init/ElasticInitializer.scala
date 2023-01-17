package ru.red.car_meta.aggregation
package init

import config.ElasticConfig

import cats.effect.kernel.Async
import cats.implicits._
import com.sksamuel.elastic4s.ElasticApi.createIndex
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl.CreateIndexHandler

class ElasticInitializer[F[_]: Async](cfg: ElasticConfig, client: ElasticClient) {
  def init: F[Unit] = Async[F].delay(client.execute{
    createIndex(cfg.carIndexName)
  })
}
