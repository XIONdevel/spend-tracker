package com.noix.spendtracker.bank.token;

import com.noix.spendtracker.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiTokenRepository extends JpaRepository<ApiToken, Long> {

    Optional<ApiToken> findByUserAndBank(User user, Bank bank);

    @Query("""
            SELECT COUNT(at) > 0
            FROM ApiToken at
            WHERE at =: tk
            """)
    boolean isExists(@Param("tk") ApiToken token);

    boolean existsByKey(String key);


}
