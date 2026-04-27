package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.Todo;
import com.itesm.domain.repository.TodoRepository;
import com.itesm.infrastructure.mapper.TodoMapper;
import com.itesm.infrastructure.persistence.entity.TodoEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TodoRepositoryImpl implements TodoRepository, PanacheRepositoryBase<TodoEntity, UUID> {
    @Override
    @Transactional
    public Todo save(Todo todo) {
        System.out.println("Entra a repository");
        TodoEntity entity= TodoMapper.toEntity(todo);
        persist(entity);
        return TodoMapper.toDomain(entity);
    }

    @Override
    public List<Todo> findAllTodos() {
        List<TodoEntity> todoEntities= findAll().stream().toList();
        List<Todo> todos = new ArrayList<>();
        for(TodoEntity todoEntity : todoEntities){
            todos.add(TodoMapper.toDomain(todoEntity));
        }
        return todos;
    }

    @Override
    @Transactional
    public List<Todo> findUserTodo(UUID usuario) {
        List<TodoEntity> todoEntities= findAll().stream().toList();
        List<Todo> todos = new ArrayList<>();
        for(TodoEntity todoEntity : todoEntities){
            if(todoEntity.getUser().equals(usuario)){
                todos.add(TodoMapper.toDomain(todoEntity));
            }
        }
        return todos;
    }

}
