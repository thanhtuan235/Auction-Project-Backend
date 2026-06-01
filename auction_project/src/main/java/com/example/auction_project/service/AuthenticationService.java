package com.example.auction_project.service;

import com.example.auction_project.dto.AuthRequest;
import com.example.auction_project.dto.AuthResponse;
import com.example.auction_project.dto.GoogleAuthRequest;
import com.example.auction_project.dto.RegisterRequest;
import com.example.auction_project.entity.User;
import com.example.auction_project.jwt.JwtService;
import com.example.auction_project.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import java.util.Collections;
import java.util.UUID;

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
                .role("BIDDER")
                .status("ACTIVE")
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

    // GoogleAuth
    @Value("${app.google.client-id}")
    private String googleClientId;

    public AuthResponse googleLogin(GoogleAuthRequest request) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(request.idToken());
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String defaultUsername = email.substring(0, email.indexOf("@"));

            var user = repository.findByEmail(email).orElse(null);

            if (user == null) {
                user = User.builder()
                        .username(defaultUsername + "_" + UUID.randomUUID().toString().substring(0, 5))
                        .email(email)
                        .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .role("BIDDER")
                        .status("ACTIVE")
                        .build();
                repository.save(user);
            }

            var jwtToken = jwtService.generateToken(user);
            return new AuthResponse(jwtToken);

        } else {
            throw new RuntimeException("Google Token không hợp lệ!");
        }
    }
}