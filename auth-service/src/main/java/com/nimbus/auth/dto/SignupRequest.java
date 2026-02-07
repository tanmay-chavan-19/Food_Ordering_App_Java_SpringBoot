package com.nimbus.auth.dto;

import lombok.Data;
import java.util.Set;

@Data
public class SignupRequest {
    private String email;
    private String password;
    private String fullName;
    private String role; // customer, owner, or admin
}