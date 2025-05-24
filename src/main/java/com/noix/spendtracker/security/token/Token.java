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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token {

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

    public static Token empty() {
        return new Token();
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
        Token token = (Token) o;
        return Objects.equals(id, token.id) && Objects.equals(user, token.user) && Objects.equals(jwt, token.jwt) && Objects.equals(expiresAt, token.expiresAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, jwt, expiresAt);
    }
}
