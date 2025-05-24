package com.noix.spendtracker.security.jwt;

import com.noix.spendtracker.security.token.Token;
import com.noix.spendtracker.security.token.TokenService;
import com.noix.spendtracker.user.User;
import com.noix.spendtracker.user.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${spring.security.jwt.secret}")
    private String SECRET;
    @Value("${spring.security.jwt.jwt-expiration}")
    private long jwtExpiration;
    @Value("${spring.security.jwt.refresh-expiration}")
    private long refreshExpiration;
    private final TokenService tokenService;
    private final UserService userService;


    public Token createToken(User user) {
        String jwt = generateJwt(user, refreshExpiration);
        Date exp = extractExpiration(jwt);
        return tokenService.createToken(user, jwt, exp);
    }

    public String createJwt(User user) {
        return generateJwt(user, jwtExpiration);
    }

    private String generateJwt(User user, long expiration) {
        return generateJwt(user, new HashMap<>(), expiration);
    }

    private String generateJwt(User user, Map<String, Object> claims, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey(), Jwts.SIG.HS512)
                .compact();
    }

    public boolean validateToken(String jwt, User user) { //todo: remove if unused
        return isExpired(jwt) && tokenService.validateToken(jwt, user);
    }

    public boolean validateJwt(String jwt, User user) {
        return isExpired(jwt)
                && userService.loadUserByUsername(user.getUsername()).equals(user);
    }

    public boolean isExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }

    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    private Date extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration);
    }

    private <T> T extractClaim(String jwt, Function<Claims, T> function) {
        return function.apply(extractAllClaims(jwt));
    }

    private Claims extractAllClaims(String jwt) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parse(jwt)
                    .accept(Jws.CLAIMS)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    }
}