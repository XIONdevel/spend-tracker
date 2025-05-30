package com.noix.spendtracker.security.token;

import com.noix.spendtracker.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUser(User user);

    List<RefreshToken> findAllByUser(User user);

    Optional<RefreshToken> findByJwt(String jwt);

    void deleteAllByUser(User user);

    void deleteByJwt(String jwt);
}
