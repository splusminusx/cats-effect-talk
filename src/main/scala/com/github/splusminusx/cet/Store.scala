package com.github.splusminusx.cet

import java.nio.charset.StandardCharsets.UTF_8

import cats.syntax.applicative._
import cats.syntax.flatMap._
import cats.syntax.functor._
import org.rocksdb.{Options, RocksDB}
import cats.effect._
import com.github.splusminusx.cet.Domain.Todo
import io.circe.parser._
import io.circe.syntax._

class Store[F[_]](db: RocksDB) {

  def get[F[_] : Sync](id: String): F[Option[Todo]] =
    Sync[F]
      .delay {
        Option(db.get(id.getBytes(UTF_8)))
      }
      .flatMap {
        case None => Option.empty[Todo].pure[F]
        case Some(bytes) =>
          Sync[F].fromEither(
            decode[Todo](new String(bytes, UTF_8)).right.map(Option(_))
          )
      }

  def put[F[_] : Sync](todo: Todo): F[Unit] =
    Sync[F].delay {
      db.put(
        todo.id.getBytes(UTF_8),
        todo.asJson.noSpaces.getBytes(UTF_8)
      )
    }
}

object Store {
  /*_*/
  def forPath[F[_] : Sync](path: String): Resource[F, Store[F]] =
    Resource
      .make(Sync[F].delay {
        val options = new Options().setCreateIfMissing(true)
        RocksDB.open(options, path)
      })(db =>
        Sync[F].delay {
          db.close()
          println("Successfully closed RocksDB store!")
        })
      .map(new Store[F](_))

  /*_*/
}
