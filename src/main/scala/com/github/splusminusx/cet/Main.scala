package com.github.splusminusx.cet

import java.util.concurrent.Executors

import cats.syntax.apply._
import cats.syntax.functor._
import cats.effect._
import cats.effect.concurrent.Ref
import com.twitter.util.Await

import scala.concurrent.ExecutionContext

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    for {
      config <- Config.fromEnv
      isReady <- Ref.of[IO, Boolean](false)
      _ <- (for {
        ec <- blockingIO[IO]
        store <- Store.forPath[IO](config.dataPath)
        server <- Server.listen[IO](config.port, store, ec, isReady)
      } yield server).use { server =>
        isReady.set(true) *> IO {
          Await.ready(server)
        }
      }
    } yield ExitCode.Success

  /*_*/
  def blockingIO[F[_] : Sync]: Resource[F, ExecutionContext] =
    Resource
      .make(Sync[F].delay {
        Executors.newCachedThreadPool()
      })(pool =>
        Sync[F].delay {
          pool.shutdown()
          println("Successfully closed blocking pool!")
        })
      .map(ExecutionContext.fromExecutor)

  /*_*/
}
