# Scala 3 REST Service

A simple REST API built with Scala 3, Http4s, and Cats Effect.

## Requirements

- JDK 11 or newer
- sbt

## Running the Application

Start the server:

```bash
sbt run
```

The server will start at [http://localhost:8080](http://localhost:8080).

## API Endpoints

### List all todos
```
GET /todos
```

### Get a specific todo
```
GET /todos/{id}
```

### Create a new todo
```
POST /todos
```
Example request body:
```json
{
  "title": "Learn Scala",
  "completed": false
}
```

### Update a todo
```
PUT /todos/{id}
```
Example request body:
```json
{
  "title": "Learn Scala 3",
  "completed": true
}
```

### Delete a todo
```
DELETE /todos/{id}
```

## Project Structure

- `models`: Data models (Todo)
- `services`: Business logic
- `routes`: HTTP endpoints
- `Server`: Application entry point 