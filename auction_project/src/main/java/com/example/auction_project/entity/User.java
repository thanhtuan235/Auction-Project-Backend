package com.example.auction_project.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;




@Entity
@Table(name = "users")
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class User implements UserDetails{

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_interests",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> interests = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return "ACTIVE".equals(status);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; 
    }

    @Override
    public boolean isEnabled() {
        return true; 
    }
}
