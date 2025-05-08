package com.example.restservice.services

import cats.effect.Ref
import cats.effect.Sync
import cats.implicits.*
import com.example.restservice.models.Todo

trait TodoService[F[_]] {
  def listTodos: F[List[Todo]]
  def getTodo(id: Long): F[Option[Todo]]
  def createTodo(todo: Todo): F[Todo]
  def updateTodo(id: Long, todo: Todo): F[Option[Todo]]
  def deleteTodo(id: Long): F[Option[Todo]]
}

object TodoService {
  def inMemory[F[_]: Sync](
    idRef: Ref[F, Long],
    todosRef: Ref[F, Map[Long, Todo]]
  ): TodoService[F] = new TodoService[F] {
    
    def listTodos: F[List[Todo]] = 
      todosRef.get.map(_.values.toList)
    
    def getTodo(id: Long): F[Option[Todo]] = 
      todosRef.get.map(_.get(id))
    
    def createTodo(todo: Todo): F[Todo] =
      for {
        id <- idRef.getAndUpdate(_ + 1)
        newTodo = todo.copy(id = Some(id))
        _ <- todosRef.update(_.updated(id, newTodo))
      } yield newTodo
    
    def updateTodo(id: Long, todo: Todo): F[Option[Todo]] =
      for {
        todos <- todosRef.get
        updated = todos.get(id).map(_ => todo.copy(id = Some(id)))
        _ <- updated.traverse(updatedTodo => todosRef.update(_.updated(id, updatedTodo)))
      } yield updated
    
    def deleteTodo(id: Long): F[Option[Todo]] =
      for {
        todos <- todosRef.get
        deleted = todos.get(id)
        _ <- deleted.traverse(_ => todosRef.update(_ - id))
      } yield deleted
  }
} 