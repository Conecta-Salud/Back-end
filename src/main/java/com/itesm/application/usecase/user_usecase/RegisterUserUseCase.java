package com.itesm.application.usecase.user_usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.itesm.application.dto.user_dto.RegisterUserDto;
import com.itesm.domain.models.user_model.User;
import com.itesm.domain.models.user_model.UserRole;
import com.itesm.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class RegisterUserUseCase {

    @Inject
    private UserRepository userRepository;

    public RegisterUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(RegisterUserDto registerUserDto) throws FirebaseAuthException {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setNombre(registerUserDto.getNombre());
        user.setApellidos(registerUserDto.getApellidos());
        user.setEmail(registerUserDto.getEmail());
        user.setActive(true);
        user.setRol(UserRole.estrategico);

        UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                .setEmail(user.getEmail())
                .setPassword(registerUserDto.getPassword());
        UserRecord userRecord= FirebaseAuth.getInstance().createUser(createRequest);
        user.setFirebaseUuid(userRecord.getUid());
        user = userRepository.create(user);
        return user;

    }
}
