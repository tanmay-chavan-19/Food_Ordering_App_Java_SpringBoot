package com.nimbus.auth.service;

import com.nimbus.auth.model.User;
import com.nimbus.auth.model.UserRole;
import com.nimbus.auth.repository.UserRepository;
import com.nimbus.auth.security.JwtUtils;
import com.nimbus.auth.dto.JwtResponse;
import com.nimbus.auth.dto.LoginRequest;
import com.nimbus.auth.dto.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    public User registerUser(SignupRequest signUpRequest) {
        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // Create new user's account
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword())) // Hashing!
                .fullName(signUpRequest.getFullName())
                .role(UserRole.valueOf(signUpRequest.getRole().toUpperCase()))
                .build();

        return userRepository.save(user);
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Error: User not found!"));

        // Check password
        if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Error: Invalid password!");
        }

        // Generate Token
        String token = jwtUtils.generateTokenFromUsername(user.getEmail());

        return new JwtResponse(
                token, 
                user.getId(), 
                user.getEmail(), 
                user.getRole().name()
        );
    }
}