package com.streamit.userservice.service;

import com.streamit.userservice.dto.*;
import com.streamit.userservice.entity.User;
import com.streamit.userservice.repository.UserRepository;
import com.streamit.userservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    public String register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername()) || userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("User already exists");
        }

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .roles(req.getRoles())
                .build();

        userRepository.save(user);
        return jwtUtil.generateToken(user.getUsername());
    }

    public String login(LoginRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        return jwtUtil.generateToken(req.getUsername());
    }
}
