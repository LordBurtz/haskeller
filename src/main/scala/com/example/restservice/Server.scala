package com.example.restservice

import cats.effect.{IO, IOApp, ExitCode, Ref}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.middleware.Logger
import com.comcast.ip4s.*
import com.example.restservice.models.Todo
import com.example.restservice.services.{TodoService, TestCaseService}
import com.example.restservice.routes.TodoRoutes
import org.http4s.StaticFile
import org.http4s.dsl.io.NotFound
import org.http4s.MediaType
import cats.effect.Sync
import cats.effect.Concurrent

object Server extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    for {
      idRef <- Ref.of[IO, Long](0L)
      todosRef <- Ref.of[IO, Map[Long, Todo]](Map.empty)
      todoService = TodoService.inMemory[IO](idRef, todosRef)
      testCaseService = TestCaseService[IO]()
      
      // Add some sample data
      _ <- todoService.createTodo(Todo(None, "Learn Scala 3", false))
      _ <- todoService.createTodo(Todo(None, "Build REST API", false))
      
      httpApp = TodoRoutes.routes(todoService, testCaseService).orNotFound
      
      // Add logging middleware
      finalHttpApp = Logger.httpApp(true, true)(httpApp)
      
      _ <- EmberServerBuilder.default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(finalHttpApp)
        .build
        .use(_ => IO.println("Server started at http://localhost:8080\nVisit the root URL to see the index page.") >> IO.never)
        .as(ExitCode.Success)
    } yield ExitCode.Success
  }

  implicit val syncF: Sync[IO] = Concurrent[IO]
} 