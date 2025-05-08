package com.example.restservice

import cats.effect.{IO, Ref}
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.circe.CirceEntityCodec.*
import com.example.restservice.models.Todo
import com.example.restservice.services.TodoService
import com.example.restservice.routes.TodoRoutes

class TodoRoutesSpec extends CatsEffectSuite {
  
  test("GET /todos returns all todos") {
    withTodoRoutes { routes =>
      val response = routes.orNotFound.run(
        Request(method = Method.GET, uri = uri"/todos")
      )
      
      val expected = List(
        Todo(Some(0), "Test Todo", false)
      )
      
      assertIO(response.flatMap(_.as[List[Todo]]), expected)
      assertIO(response.map(_.status), Status.Ok)
    }
  }
  
  test("POST /todos creates a new todo") {
    withTodoRoutes { routes =>
      val newTodo = Todo(None, "New Todo", false)
      val response = routes.orNotFound.run(
        Request(method = Method.POST, uri = uri"/todos")
          .withEntity(newTodo)
      )
      
      val expectedTodo = Todo(Some(1), "New Todo", false)
      
      assertIO(response.flatMap(_.as[Todo]), expectedTodo)
      assertIO(response.map(_.status), Status.Created)
    }
  }
  
  private def withTodoRoutes(test: HttpRoutes[IO] => IO[Unit]): Unit = {
    val resource = for {
      idRef <- Ref.of[IO, Long](0L)
      todosRef <- Ref.of[IO, Map[Long, Todo]](Map.empty)
      _ <- todosRef.update(_.updated(0L, Todo(Some(0), "Test Todo", false)))
      todoService = TodoService.inMemory[IO](idRef, todosRef)
    } yield TodoRoutes.routes(todoService)
    
    resource.flatMap(routes => test(routes)).unsafeRunSync()
  }
} 