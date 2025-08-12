package com.noix.spendtracker.user;

import com.noix.spendtracker.user.role.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

@Data
@Entity
@Table(name = "app_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean enabled = true;

    public static User empty() {
        return new User();
    }

    public boolean isEmpty() {
        return id == null
                && username == null
                && password == null
                && email == null
                && role == null;
    }

    public boolean isPresent() {
        return !isEmpty();
    }
    
    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public User(String username, String password, String email, Role role, boolean enabled) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
    }

    public User(Long id, String username, String password, String email, Role role, boolean enabled) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
    }

    public User() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return enabled == user.enabled
                && Objects.equals(id, user.id)
                && Objects.equals(username, user.username)
                && Objects.equals(password, user.password)
                && Objects.equals(email, user.email)
                && role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, email, role, enabled);
    }
}
