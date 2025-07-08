// package com.streamit.user_service.dto;

// import java.util.Set;

// import lombok.Data;

// @Data
// public class RegisterRequest {
//     private String username;
//     private String email;
//     private String password;
//     private Set<String> roles;
// }

package com.streamit.user_service.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Set<String> roles;
}