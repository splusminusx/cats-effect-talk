package com.github.splusminusx.cet

import cats.effect._
import cats.effect.concurrent.Ref
import cats.syntax.apply._
import cats.syntax.functor._
import com.github.splusminusx.cet.Domain.Todo
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, ListeningServer, Service}
import io.finch._
import io.finch.circe._

import scala.concurrent.ExecutionContext

class Server[F[_]: Effect](
    store: Store[F],
    blockingIO: ExecutionContext,
    isReady: Ref[F, Boolean]
)(implicit cs: ContextShift[F])
    extends Endpoint.Module[F] {

  /*_*/
  final val health: Endpoint[F, Boolean] = get("health") {
    isReady.get.map {
      case true  => Ok(true)
      case false => ServiceUnavailable(new Exception("Not ready!"))
    }
  }

  final val getTodo: Endpoint[F, Todo] = get("todo" :: path[String]) {
    id: String =>
      cs.evalOn(blockingIO) {
        store.get(id).map {
          case Some(t) => Ok(t)
          case None    => NotFound(new Exception(s"Todo with id $id not found."))
        }
      }
  }

  final val postTodo: Endpoint[F, Todo] = post("todo" :: jsonBody[Todo]) {
    t: Todo =>
      cs.evalOn(blockingIO) { store.put(t).as(Created(t)) }
  }
  /*_*/

  final def toService: Service[Request, Response] =
    Bootstrap
      .serve[Application.Json](health :+: getTodo :+: postTodo)
      .toService
}

object Server {

  def listen[F[_]: Effect](
      port: Int,
      store: Store[F],
      blockingIO: ExecutionContext,
      isReady: Ref[F, Boolean]
  )(implicit cs: ContextShift[F]): Resource[F, ListeningServer] =
    Resource.make(Effect[F].delay {
      val service = new Server[F](store, blockingIO, isReady)
      Http.serve(s":$port", service.toService)
    })(server =>
      Effect[F]
        .async[Unit] { k =>
          server
            .close()
            .onSuccess(_ => k(Right(())))
            .onFailure(ex => k(Left(ex)))
        } *> Sync[F].delay {
        println("Successfully closed HTTP server!")
    })

}
