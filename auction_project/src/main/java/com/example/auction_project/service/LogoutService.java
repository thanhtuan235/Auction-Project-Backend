package com.example.auction_project.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import com.example.auction_project.repository.UserRepository;
import com.example.auction_project.jwt.JwtService;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);

        if (username != null) {
            var user = userRepository.findByUsername(username).orElse(null);

            if (user != null) {
                user.setTokenVersion(user.getTokenVersion() + 1);
                userRepository.save(user);
            }
        }

        SecurityContextHolder.clearContext();
    }
}