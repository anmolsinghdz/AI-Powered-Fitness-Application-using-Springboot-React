package com.fitness.userservice.service;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository repo;

    public UserResponse getUserProfile(String userId) {
        User user = repo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);

    }

    public UserResponse register(RegisterRequest request) {

        if(repo.existsByEmail(request.getEmail())){
            User existingUser=repo.findByEmail(request.getEmail());

            return mapToResponse(existingUser);

        }
        User user=new User();

        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setKeycloakId(request.getKeycloakId());
        user.setFirstname(request.getFirstName());
        user.setLastname(request.getLastName());

        User savedUser = repo.save(user);

        return mapToResponse(savedUser);
    }

    public UserResponse mapToResponse(User savedUser) {
        UserResponse ur=new UserResponse();
        ur.setId(savedUser.getId());
        ur.setKeycloakId(savedUser.getKeycloakId());
        ur.setEmail(savedUser.getEmail());
        ur.setPassword(savedUser.getPassword());
        ur.setFirstname(savedUser.getFirstname());
        ur.setLastname(savedUser.getLastname());
        ur.setCreatedAt(savedUser.getCreatedAt());
        ur.setUpdatedAt(savedUser.getUpdatedAt());
        return ur;
    }

    public Boolean existByUserId(String userId) {
        log.info("Calling User Validation API for userId: {}", userId);
        return repo.existsByKeycloakId(userId);
    }
}
