package com.noix.spendtracker.security.token;

import com.noix.spendtracker.user.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    public Token extractToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("token")) {
                    return tokenRepository.findByJwt(c.getValue())
                            .orElse(Token.empty());
                }
            }
        }
        return Token.empty();
    }

    public boolean validateToken(String jwt, User user) {
        Optional<Token> storedToken = tokenRepository.findByJwt(jwt);
        if (storedToken.isEmpty()) {
            return false;
        }
        Token token = storedToken.get();
        return token.getUser().equals(user) && token.getJwt().equals(jwt);
    }

    public Token getValidToken(User user) {
        List<Token> tokens = tokenRepository.findAllByUser(user);

        if (tokens.isEmpty()) {
            return Token.empty();
        }
        for (Token t : tokens) {
            if (t.isExpired()) {
                tokenRepository.deleteById(t.getId());
            } else {
                return t;
            }
        }
        return Token.empty();
    }

    public Token createToken(User user, String jwt, Date exp) {
        Token token = Token.builder()
                .user(user)
                .jwt(jwt)
                .expiresAt(exp)
                .build();
        return tokenRepository.save(token);
    }
}
