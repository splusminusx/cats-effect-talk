package com.github.splusminusx.cet

import cats.effect.IO

case class Config(
    port: Int,
    dataPath: String
)

object Config {
  val fromEnv = IO {
    Config(
      sys.env.getOrElse("SERVER_PORT", "8080").toInt,
      sys.env.getOrElse("DATA_PATH", "/tmp/cats-effect-talk")
    )
  }
}
