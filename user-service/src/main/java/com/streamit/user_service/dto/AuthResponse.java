// package com.streamit.user_service.dto;

// import lombok.AllArgsConstructor;
// import lombok.Data;

// @Data
// @AllArgsConstructor
// public class AuthResponse {
//     private String token;
// }

package com.streamit.user_service.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String message;
    private String type;
    private long timestamp;
    
    public AuthResponse(String token) {
        this.token = token;
        this.type = "Bearer";
        this.timestamp = System.currentTimeMillis();
    }
    
    public AuthResponse(String token, String message) {
        this.token = token;
        this.message = message;
        this.type = "Bearer";
        this.timestamp = System.currentTimeMillis();
    }
}