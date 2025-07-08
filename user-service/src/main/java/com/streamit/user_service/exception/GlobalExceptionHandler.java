// package com.streamit.user_service.exception;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestControllerAdvice
// public class GlobalExceptionHandler {
    
//     @ExceptionHandler(UsernameNotFoundException.class)
//     public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
//         ErrorResponse error = new ErrorResponse(
//             "USER_NOT_FOUND",
//             e.getMessage(),
//             HttpStatus.NOT_FOUND.value()
//         );
//         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//     }
    
//     @ExceptionHandler(BadCredentialsException.class)
//     public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
//         ErrorResponse error = new ErrorResponse(
//             "INVALID_CREDENTIALS",
//             "Invalid username or password",
//             HttpStatus.UNAUTHORIZED.value()
//         );
//         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
//     }
    
//     @ExceptionHandler(RuntimeException.class)
//     public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
//         ErrorResponse error = new ErrorResponse(
//             "INTERNAL_ERROR",
//             e.getMessage(),
//             HttpStatus.INTERNAL_SERVER_ERROR.value()
//         );
//         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//     }
    
//     @ExceptionHandler(Exception.class)
//     public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
//         ErrorResponse error = new ErrorResponse(
//             "UNEXPECTED_ERROR",
//             "An unexpected error occurred: " + e.getMessage(),
//             HttpStatus.INTERNAL_SERVER_ERROR.value()
//         );
//         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//     }
    
//     // Error response class
//     public static class ErrorResponse {
//         private String error;
//         private String message;
//         private int status;
//         private long timestamp;
        
//         public ErrorResponse(String error, String message, int status) {
//             this.error = error;
//             this.message = message;
//             this.status = status;
//             this.timestamp = System.currentTimeMillis();
//         }
        
//         // Getters
//         public String getError() { return error; }
//         public String getMessage() { return message; }
//         public int getStatus() { return status; }
//         public long getTimestamp() { return timestamp; }
//     }
// }


package com.streamit.user_service.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        });
        
        // Determine if it's login or register based on the fields
        String message = errors.stream().anyMatch(err -> err.contains("email")) 
                ? "Registration validation failed" 
                : "Login validation failed";
        
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
            "VALIDATION_FAILED",
            message,
            HttpStatus.BAD_REQUEST.value(),
            errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
        ErrorResponse error = new ErrorResponse(
            "USER_NOT_FOUND",
            "Invalid username or password",
            HttpStatus.UNAUTHORIZED.value()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        ErrorResponse error = new ErrorResponse(
            "INVALID_CREDENTIALS",
            "Invalid username or password",
            HttpStatus.UNAUTHORIZED.value()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    // Handle duplicate user registration
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        if (e.getMessage().contains("already exists") || e.getMessage().contains("duplicate")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("USER_ALREADY_EXISTS", e.getMessage()));
        }
        if (e.getMessage().contains("credentials") || e.getMessage().contains("password")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("AUTHENTICATION_FAILED", "Invalid username or password"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", e.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        ErrorResponse error = new ErrorResponse(
            "UNEXPECTED_ERROR",
            "An unexpected error occurred",
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    // Validation error response class
    public static class ValidationErrorResponse {
        private String error;
        private String message;
        private int status;
        private long timestamp;
        private List<String> validationErrors;
        
        public ValidationErrorResponse(String error, String message, int status, List<String> validationErrors) {
            this.error = error;
            this.message = message;
            this.status = status;
            this.timestamp = System.currentTimeMillis();
            this.validationErrors = validationErrors;
        }
        
        // Getters
        public String getError() { return error; }
        public String getMessage() { return message; }
        public int getStatus() { return status; }
        public long getTimestamp() { return timestamp; }
        public List<String> getValidationErrors() { return validationErrors; }
    }
    
    // Error response class
    public static class ErrorResponse {
        private String error;
        private String message;
        private int status;
        private long timestamp;
        
        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
        
        public ErrorResponse(String error, String message, int status) {
            this.error = error;
            this.message = message;
            this.status = status;
            this.timestamp = System.currentTimeMillis();
        }
        
        // Getters
        public String getError() { return error; }
        public String getMessage() { return message; }
        public int getStatus() { return status; }
        public long getTimestamp() { return timestamp; }
    }
}