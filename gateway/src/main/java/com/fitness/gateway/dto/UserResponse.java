package com.fitness.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String id;
    private String keycloakId;
    private String email;
    private String password;
    private String firstname;
    private String lastname;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
