// // package com.streamit.user_service.controller;

// // import org.springframework.web.bind.annotation.PostMapping;
// // import org.springframework.web.bind.annotation.RequestBody;
// // import org.springframework.web.bind.annotation.RequestMapping;
// // import org.springframework.web.bind.annotation.RestController;

// // import com.streamit.user_service.dto.AuthResponse;
// // import com.streamit.user_service.dto.LoginRequest;
// // import com.streamit.user_service.dto.RegisterRequest;
// // import com.streamit.user_service.service.AuthService;

// // import lombok.RequiredArgsConstructor;

// // @RestController
// // @RequestMapping("/api/auth")
// // @RequiredArgsConstructor
// // public class AuthController {

// //     private final AuthService authService;

// //     @PostMapping("/register")
// //     public AuthResponse register(@RequestBody RegisterRequest request) {
// //         return new AuthResponse(authService.register(request));
// //     }

// //     @PostMapping("/login")
// //     public AuthResponse login(@RequestBody LoginRequest request) {
// //         return new AuthResponse(authService.login(request));
// //     }
// // }



// package com.streamit.user_service.controller;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.streamit.user_service.dto.AuthResponse;
// import com.streamit.user_service.dto.LoginRequest;
// import com.streamit.user_service.dto.RegisterRequest;
// import com.streamit.user_service.service.AuthService;

// import lombok.RequiredArgsConstructor;

// @RestController
// @RequestMapping("/api/auth")
// @RequiredArgsConstructor
// public class AuthController {
    
//     private final AuthService authService;
    
//     @PostMapping("/register")
//     public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
//         try {
//             String token = authService.register(request);
//             return ResponseEntity.ok(new AuthResponse(token));
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                     .body(new ErrorResponse("Registration failed", e.getMessage()));
//         }
//     }
    
//     @PostMapping("/login")
//     public ResponseEntity<?> login(@RequestBody LoginRequest request) {
//         try {
//             String token = authService.login(request);
//             return ResponseEntity.ok(new AuthResponse(token));
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                     .body(new ErrorResponse("Login failed", e.getMessage()));
//         }
//     }
    
//     // Inner class for error responses
//     public static class ErrorResponse {
//         private String error;
//         private String message;
//         private long timestamp;
        
//         public ErrorResponse(String error, String message) {
//             this.error = error;
//             this.message = message;
//             this.timestamp = System.currentTimeMillis();
//         }
        
//         // Getters
//         public String getError() { return error; }
//         public String getMessage() { return message; }
//         public long getTimestamp() { return timestamp; }
//     }
// }


package com.streamit.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.streamit.user_service.dto.AuthResponse;
import com.streamit.user_service.dto.LoginRequest;
import com.streamit.user_service.dto.RegisterRequest;
import com.streamit.user_service.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            String token = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AuthResponse(token, "User registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("REGISTRATION_FAILED", e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = authService.login(request);
            return ResponseEntity.ok(new AuthResponse(token, "Login successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("LOGIN_FAILED", e.getMessage()));
        }
    }
    
    // Error response class
    public static class ErrorResponse {
        private String error;
        private String message;
        private long timestamp;
        
        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
        
        // Getters
        public String getError() { return error; }
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }
}