package com.itesm.application.usecase;

import com.itesm.application.dto.CreateTodoDto;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.domain.models.Todo;
import com.itesm.domain.repository.TodoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sound.midi.SysexMessage;
import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class CreateTodoUseCase {
    private final TodoRepository todoRepository;
    private final AuthenticatedUserContext authenticatedUserContext;

    @Inject
    public CreateTodoUseCase(TodoRepository todoRepository, AuthenticatedUserContext authenticatedUserContext) {
        this.todoRepository = todoRepository;
        this.authenticatedUserContext= authenticatedUserContext;
    }

    public Todo execute(CreateTodoDto createTodoDto) {
        System.out.println("Desde use case: "+ authenticatedUserContext.getCurrentUser().getFullName());
        Todo todo = new Todo();
        todo.setUuid(UUID.randomUUID());
        todo.setCreatedAt(LocalDateTime.now());
        todo.setTitle(createTodoDto.getTitle());
        todo.setDescription(createTodoDto.getDescription());
        todo.setCompleted(false);
        todo.setUsuario(authenticatedUserContext.getCurrentUser().getUserId());
        return todoRepository.save(todo);
    }
}
