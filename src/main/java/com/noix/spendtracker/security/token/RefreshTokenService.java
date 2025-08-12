package com.noix.spendtracker.security.token;

import com.noix.spendtracker.user.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository tokenRepository;

    public RefreshToken extractToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("token")) {
                    Optional<RefreshToken> opToken = tokenRepository.findByJwt(c.getValue());
                    if (opToken.isPresent()) {
                        RefreshToken refreshToken = opToken.get();
                        if (refreshToken.isExpired()) {
                            tokenRepository.delete(refreshToken);
                            return RefreshToken.empty();
                        } else {
                            return refreshToken;
                        }
                    }
                }
            }
        }
        return RefreshToken.empty();
    }

    public boolean validateToken(String jwt, User user) {
        Optional<RefreshToken> storedToken = tokenRepository.findByJwt(jwt);
        if (storedToken.isEmpty()) {
            return false;
        }
        RefreshToken refreshToken = storedToken.get();
        return refreshToken.getUser().equals(user) && refreshToken.getJwt().equals(jwt);
    }

    public RefreshToken getValidToken(User user) {
        List<RefreshToken> refreshTokens = tokenRepository.findAllByUser(user);

        if (refreshTokens.isEmpty()) {
            return RefreshToken.empty();
        }
        for (RefreshToken t : refreshTokens) {
            if (t.isExpired()) {
                tokenRepository.deleteById(t.getId());
            } else {
                return t;
            }
        }
        return RefreshToken.empty();
    }

    public void deleteAllByUser(User user) {
        if (user.isEmpty()) throw new IllegalArgumentException("User is empty");
        tokenRepository.deleteAllByUser(user);
    }

    public void deleteToken(String jwt) {
        tokenRepository.deleteByJwt(jwt);
    }

    public RefreshToken createToken(User user, String jwt, Date exp) {
        RefreshToken refreshToken = new RefreshToken(user, jwt, exp);
        return tokenRepository.save(refreshToken);
    }
}
