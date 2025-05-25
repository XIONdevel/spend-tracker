package com.noix.spendtracker.bank;

import com.noix.spendtracker.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Table(name = "api_token")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String key;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private Bank bank;


    public static ApiToken empty() {
        return new ApiToken();
    }

    public boolean isEmpty() {
        return key == null
                && user == null
                && bank == null;
    }

    public boolean isPresent() {
        return !isEmpty();
    }

    @Override
    public String toString() {
        return "ApiToken{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", user=" + user +
                ", bank=" + bank +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiToken apiToken)) return false;
        return Objects.equals(id, apiToken.id)
                && Objects.equals(key, apiToken.key)
                && Objects.equals(user, apiToken.user)
                && bank == apiToken.bank;
    }

    @Override
    public int hashCode() {
        return (key.length() + user.getEmail().length() * 7) + user.getPassword().length();
    }
}
