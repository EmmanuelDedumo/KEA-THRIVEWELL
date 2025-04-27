package com.KEA.g3.ThriveWell.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Table(name = "users")
@Entity
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, length = 100, nullable = false)
    private String email;

    @JsonIgnore // Prevent password from being serialized
    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @JsonIgnore // Prevent UserDetails method from being serialized
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @JsonIgnore // Prevent UserDetails method from being serialized
    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore // Prevent UserDetails method from being serialized
    @Override
    public String getUsername() {
        return email;
    }

    @JsonIgnore // Prevent UserDetails method from being serialized
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore // Prevent UserDetails method from being serialized
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore // Prevent UserDetails method from being serialized
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore // Prevent UserDetails method from being serialized
    @Override
    public boolean isEnabled() {
        return true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}