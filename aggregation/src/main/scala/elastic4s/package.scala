package ru.red.car_meta.aggregation

import cats.effect.kernel.Async
import com.sksamuel.elastic4s.{ElasticRequest, Executor, Functor, HttpClient}

package object elastic4s {
  implicit def CatsFunctor[F[_]: cats.Functor]: Functor[F] =
    new Functor[F] {
      override def map[A, B](fa: F[A])(f: A => B): F[B] =
        cats.Functor[F].map(fa)(f)
    }

  implicit def AsyncExecutor[F[_]: Async]: Executor[F] =
    (client: HttpClient, request: ElasticRequest) => Async[F].fromFuture(
      Async[F].delay(Executor.FutureExecutor.exec(client, request))
    )
}
