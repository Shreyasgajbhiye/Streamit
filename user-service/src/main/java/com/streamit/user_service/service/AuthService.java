// package com.streamit.user_service.service;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;
// import com.streamit.user_service.dto.LoginRequest;
// import com.streamit.user_service.dto.RegisterRequest;
// import com.streamit.user_service.entity.User;
// import com.streamit.user_service.repository.UserRepository;
// import com.streamit.user_service.security.JwtUtil;
// import lombok.RequiredArgsConstructor;
// @Service
// @RequiredArgsConstructor
// public class AuthService {
//     private final UserRepository userRepository;
//     private final PasswordEncoder encoder;
//     private final JwtUtil jwtUtil;
//     private final AuthenticationManager authManager;
//     // public String register(RegisterRequest req) {
//     //     if (userRepository.existsByUsername(req.getUsername()) || userRepository.existsByEmail(req.getEmail())) {
//     //         throw new RuntimeException("User already exists");
//     //     }
//     //     User user = User.builder()
//     //             .username(req.getUsername())
//     //             .email(req.getEmail())
//     //             .password(encoder.encode(req.getPassword()))
//     //             .roles(req.getRoles())
//     //             .build();
//     //     userRepository.save(user);
//     //     return jwtUtil.generateToken(user.getUsername());
//     // }
//    public String register(RegisterRequest request) {
//     if (userRepository.findByEmail(request.getEmail()).isPresent()) {
//         throw new RuntimeException("Email already exists");
//     }
//     if (userRepository.findByUsername(request.getUsername()).isPresent()) {
//         throw new RuntimeException("Username already exists");
//     }
//     User user = new User();
//     user.setUsername(request.getUsername());
//     user.setEmail(request.getEmail());
//     user.setPassword(encoder.encode(request.getPassword()));
//     user.setRoles(request.getRoles());
//     userRepository.save(user);
// System.err.println("before jwt" + user);
//     return jwtUtil.generateToken(user.getUsername());
// }
//     public String login(LoginRequest req) {
//         authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
//         return jwtUtil.generateToken(req.getUsername());
//     }
// }
package com.streamit.user_service.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.streamit.user_service.dto.LoginRequest;
import com.streamit.user_service.dto.RegisterRequest;
import com.streamit.user_service.entity.User;
import com.streamit.user_service.repository.UserRepository;
import com.streamit.user_service.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    public String register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRoles(request.getRoles());

        userRepository.save(user);
        System.err.println("before jwt" + user);
        return jwtUtil.generateToken(user.getUsername());
    }

    public String login(LoginRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        return jwtUtil.generateToken(req.getUsername());
    }
}
