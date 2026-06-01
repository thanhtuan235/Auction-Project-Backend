package com.example.auction_project.service;

import com.example.auction_project.dto.Users.InterestRequest;
import com.example.auction_project.dto.Users.UserResponse;
import com.example.auction_project.dto.Users.UserUpdateRequest;
import com.example.auction_project.entity.User;
import com.example.auction_project.exception.ResourceNotFoundException;
import com.example.auction_project.repository.CategoryRepository;
import com.example.auction_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.auction_project.entity.Category;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found");
        }
        return users.stream().map(this::mapToUserResponse).toList();
    }

    public UserResponse findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::mapToUserResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    @Transactional
    public UserResponse updateRole(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setRole(request.role());
        return mapToUserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateStatus(UUID id, String status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setStatus(status);
        return mapToUserResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public void updateInterests(User user, InterestRequest request) {
        User managedUser = userRepository.findById(user.getId())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Category> categories = categoryRepository.findAllById(request.categoryIds());

        managedUser.getInterests().clear();
        managedUser.getInterests().addAll(categories);

        userRepository.save(managedUser);
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}