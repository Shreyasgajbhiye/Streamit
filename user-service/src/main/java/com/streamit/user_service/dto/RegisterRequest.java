package com.streamit.user_service.dto;

import java.util.Set;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private Set<String> roles;
}
