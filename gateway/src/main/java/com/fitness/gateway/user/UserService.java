package com.fitness.gateway.user;

import com.fitness.gateway.dto.RegisterRequest;
import com.fitness.gateway.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userId) {
        log.info("Calling User Validation API for userId: {}", userId);
        return userServiceWebClient.get()
                .uri("/api/users/{userId}/validate", userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(WebClientResponseException.class, e->{
                    if(e.getStatusCode()== HttpStatus.NOT_FOUND){
                        return Mono.error(new RuntimeException("USER NOT FOUND: " + userId));
                    }
                    else if(e.getStatusCode()==HttpStatus.BAD_REQUEST){
                        return Mono.error(new RuntimeException("INVALID REQUEST: " + userId));
                    }
                    return Mono.error(new RuntimeException("UNEXPECTED ERROR: " + userId));
                });
    }

    public Mono<UserResponse> registerUser(RegisterRequest request) {
        log.info("Calling User Registration API for userEmail: {}", request.getEmail());
        return userServiceWebClient.post()
                .uri("/api/users/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, e->{
                    if(e.getStatusCode()== HttpStatus.BAD_REQUEST){
                        return Mono.error(new RuntimeException("BAD_REQUEST: " + e.getMessage()));
                    }
                    else if(e.getStatusCode()==HttpStatus.INTERNAL_SERVER_ERROR){
                        return Mono.error(new RuntimeException("INTERNAL_SERVER_ERROR: " + e.getMessage()));
                    }
                    return Mono.error(new RuntimeException("UNEXPECTED ERROR: " + e.getMessage()));
                });
    }
}
