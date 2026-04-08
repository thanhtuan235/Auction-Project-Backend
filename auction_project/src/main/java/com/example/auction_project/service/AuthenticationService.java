package com.example.auction_project.service;

import com.example.auction_project.dto.AuthRequest;
import com.example.auction_project.dto.AuthResponse;
import com.example.auction_project.dto.RegisterRequest;
import com.example.auction_project.entity.User;
import com.example.auction_project.jwt.JwtService;
import com.example.auction_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
    
        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();
        
        repository.save(user);
        
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        
        var user = repository.findByUsername(request.username())
                .orElseThrow(); 
        
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }
}