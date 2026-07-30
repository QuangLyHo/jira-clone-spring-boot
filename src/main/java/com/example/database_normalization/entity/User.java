package com.example.database_normalization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "email is required")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "first name is required")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "last name is required")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    public User() {}

    public User(String email, String fName, String lName) {
        this.email = email;
        this.firstName = fName;
        this.lastName = lName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return this.firstName; }
    public void setFirstName(String fName) { this.firstName = fName; }

    public String getLastName() { return this.lastName; }
    public void setLastName(String lName) { this.lastName = lName; }

    public String getEmail() { return this.email; }
    public void setEmail(String email) { this.email = email; }

}
