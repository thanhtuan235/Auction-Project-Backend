package com.example.auction_project.entity;

import java.util.Set;
import java.util.HashSet;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Column(unique = true, length = 100)
    private String slug;

    // FOR statistic
    @ManyToMany(mappedBy = "interestedCategories")
    @Builder.Default
    private Set<User> interestedUsers = new HashSet<>();
}
