package com.itesm.infrastructure.mapper;

import com.itesm.domain.models.Todo;
import com.itesm.infrastructure.persistence.entity.TodoEntity;

public class TodoMapper {

    public static TodoEntity toEntity(Todo todo) {
        TodoEntity todoEntity = new TodoEntity();
        todoEntity.setId(todo.getUuid());
        todoEntity.setTitle(todo.getTitle());
        todoEntity.setDescription(todo.getDescription());
        todoEntity.setCompleted(todo.isCompleted());
        todoEntity.setCreatedAt(todo.getCreatedAt());
        todoEntity.setUser(todo.getUsuario());
        return todoEntity;
    }

    public static Todo toDomain(TodoEntity todoEntity) {
        Todo todo = new Todo();
        todo.setUuid(todoEntity.getId());
        todo.setTitle(todoEntity.getTitle());
        todo.setDescription(todoEntity.getDescription());
        todo.setCompleted(todoEntity.isCompleted());
        todo.setCreatedAt(todoEntity.getCreatedAt());
        todo.setUsuario(todoEntity.getUser());
        return todo;
    }
}
