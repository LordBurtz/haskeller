package com.example.restservice.routes

import cats.effect.{Concurrent, Sync}
import cats.syntax.all.*
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.headers.*
import org.http4s.{MediaType, Charset}
import com.example.restservice.models.Todo
import com.example.restservice.services.TodoService
import org.http4s.EntityEncoder

object TodoRoutes {
  // Read the HTML content at compile time as a raw String
  private val indexHtmlContent = {
    val stream = getClass.getClassLoader.getResourceAsStream("index-page.html")
    try {
      new String(stream.readAllBytes().filterNot((byte) => byte == '\n'))
    } finally {
      if (stream != null) stream.close()
    }
  }

  def routes[F[_]](todoService: TodoService[F])(using F: Concurrent[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl.*

    HttpRoutes.of[F] {
      // Serve static index page at root with proper content type
      case GET -> Root =>
        Ok(indexHtmlContent).map(_.withContentType(`Content-Type`(MediaType.text.html, Charset.`UTF-8`)))
        
      case GET -> Root / "todos" =>
        for {
          todos <- todoService.listTodos
          resp <- Ok(todos)
        } yield resp

      case GET -> Root / "todos" / LongVar(id) =>
        for {
          todo <- todoService.getTodo(id)
          resp <- todo.fold(NotFound())(Ok(_))
        } yield resp

      case req @ POST -> Root / "todos" =>
        for {
          todo <- req.as[Todo]
          created <- todoService.createTodo(todo)
          resp <- Created(created)
        } yield resp

      case req @ PUT -> Root / "todos" / LongVar(id) =>
        for {
          todo <- req.as[Todo]
          updated <- todoService.updateTodo(id, todo)
          resp <- updated.fold(NotFound())(Ok(_))
        } yield resp

      case DELETE -> Root / "todos" / LongVar(id) =>
        for {
          deleted <- todoService.deleteTodo(id)
          resp <- deleted.fold(NotFound())(Ok(_))
        } yield resp
    }
  }
} 