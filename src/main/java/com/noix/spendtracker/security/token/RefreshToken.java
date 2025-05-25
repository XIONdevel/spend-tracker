package com.noix.spendtracker.security.token;


import com.noix.spendtracker.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

@Data
@Entity
@Table(name = "refresh_token")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;
    @Column(unique = true)
    private String jwt;

    @Column(name = "expires_at", nullable = false)
    private Date expiresAt;

    public boolean isExpired() {
        return expiresAt.after(new Date());
    }

    public static RefreshToken empty() {
        return new RefreshToken();
    }

    public boolean isEmpty() {
        return  user == null
                || jwt == null
                || expiresAt == null;
    }

    public boolean isPresent() {
        return !isEmpty();
    }

    @Override
    public String toString() {
        return "Token{" +
                "id=" + id +
                ", user=" + user +
                ", jwt='" + jwt + '\'' +
                ", expiresAt=" + expiresAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken refreshToken = (RefreshToken) o;
        return Objects.equals(id, refreshToken.id) && Objects.equals(user, refreshToken.user) && Objects.equals(jwt, refreshToken.jwt) && Objects.equals(expiresAt, refreshToken.expiresAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, jwt, expiresAt);
    }
}
