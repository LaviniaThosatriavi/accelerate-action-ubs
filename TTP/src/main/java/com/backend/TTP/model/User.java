package com.backend.TTP.model;

import jakarta.persistence.*;
import lombok.Data;  // Add this import

@Entity
@Table(name = "app_users")
@Data  // Now recognized
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String username;
    
    private String password;
}