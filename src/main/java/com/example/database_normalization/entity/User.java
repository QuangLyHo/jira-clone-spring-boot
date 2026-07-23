package com.example.database_normalization.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @ManyToMany(mappedBy = "assignees", fetch = FetchType.LAZY)
    private Set<Task> tasks = new HashSet<>();

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

    public Set<Task> getTasks() { return tasks; }
    public void setTasks(Set<Task> tasks) { this.tasks = tasks; }
}
