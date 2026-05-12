package com.itesm.application.usecase.user;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.itesm.application.dto.user.CreateUserDto;
import com.itesm.domain.models.user.User;
import com.itesm.domain.models.user.UserRole;
import com.itesm.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class CreateUserUseCase {

    @Inject
    private UserRepository userRepository;

    public CreateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(CreateUserDto createUserDto) throws FirebaseAuthException {
        User user = new User();

        user.setId(UUID.randomUUID());
        user.setDepartmentId(createUserDto.getDepartmentId());
        user.setFirstName(createUserDto.getFirstName());
        user.setLastName(createUserDto.getLastName());
        user.setEmail(createUserDto.getEmail());
        user.setActive(true);
        user.setRole(UserRole.strategic);

        UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                .setEmail(user.getEmail())
                .setPassword(createUserDto.getPassword())
                .setDisplayName(user.getFirstName() + " " + user.getLastName());

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);

        user.setFirebaseUuid(userRecord.getUid());

        return userRepository.create(user);
    }
}
