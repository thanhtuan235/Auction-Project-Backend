package com.example.auction_project.controller;

import com.example.auction_project.dto.AuthRequest;
import com.example.auction_project.dto.AuthResponse;
import com.example.auction_project.dto.GoogleAuthRequest;
import com.example.auction_project.dto.RegisterRequest;
import com.example.auction_project.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleAuthenticate(@RequestBody GoogleAuthRequest request) throws Exception {
        return ResponseEntity.ok(service.googleLogin(request));
    }
}