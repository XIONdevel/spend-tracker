package com.noix.spendtracker.security.token;

import com.noix.spendtracker.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByUser(User user);

    List<Token> findAllByUser(User user);

    Optional<Token> findByJwt(String jwt);
}
