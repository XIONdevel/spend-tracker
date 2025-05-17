package com.noix.spendtracker.security.token;


import com.noix.spendtracker.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private String jwt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

}
