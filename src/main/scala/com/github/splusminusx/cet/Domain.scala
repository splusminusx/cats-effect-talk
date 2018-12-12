package com.github.splusminusx.cet

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

object Domain {
  final case class Todo(id: String, title: String, tags: Seq[String])

  object Todo {
    implicit val decoder: Decoder[Todo] = deriveDecoder
    implicit val encoder: Encoder[Todo] = deriveEncoder
  }
}
