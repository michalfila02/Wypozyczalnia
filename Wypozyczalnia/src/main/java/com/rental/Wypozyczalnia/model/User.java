package com.rental.Wypozyczalnia.model;

import com.rental.Wypozyczalnia.security.CustomUserDetails;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;

@Entity
@Table(name = "users", schema = "mydb", indexes = {
        @Index(name = "Role_Role", columnList = "Role_Role")
})
public class User {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "email_verified_at")
    private Instant emailVerifiedAt;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "Role_Role", nullable = false)
    private Role roleRole;

    @Column(name = "remember_token", length = 100)
    private String rememberToken;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "stworzone_przez", length = 45, updatable = false)
    private String stworzonePrzez;

    @Column(name = "zmienione_przez", length = 45)
    private String zmienionePrzez;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.stworzonePrzez = getCurrentUser();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
        this.zmienionePrzez = getCurrentUser();
    }

    private String getCurrentUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                return userDetails.getUser().getName(); // Use name instead of email
            }
            return auth != null ? auth.getName() : "SYSTEM";
        } catch (Exception e) {
            return "SYSTEM";
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(Instant emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRoleRole() {
        return roleRole;
    }

    public void setRoleRole(Role roleRole) {
        this.roleRole = roleRole;
    }

    public String getRememberToken() {
        return rememberToken;
    }

    public void setRememberToken(String rememberToken) {
        this.rememberToken = rememberToken;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStworzonePrzez() {
        return stworzonePrzez;
    }

    public void setStworzonePrzez(String stworzonePrzez) {
        this.stworzonePrzez = stworzonePrzez;
    }

    public String getZmienionePrzez() {
        return zmienionePrzez;
    }

    public void setZmienionePrzez(String zmienionePrzez) {
        this.zmienionePrzez = zmienionePrzez;
    }

}