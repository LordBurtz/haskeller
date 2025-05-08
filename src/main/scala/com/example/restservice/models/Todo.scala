package com.example.restservice.models

import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto.*

case class Todo(id: Option[Long], title: String, completed: Boolean)

object Todo {
  given Decoder[Todo] = deriveDecoder[Todo]
  given Encoder[Todo] = deriveEncoder[Todo]
} 