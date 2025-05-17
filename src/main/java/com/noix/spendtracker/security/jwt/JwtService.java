package com.noix.spendtracker.security.jwt;

import com.noix.spendtracker.user.User;
import io.jsonwebtoken.Claims;
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
    @Value("${spring.security.jwt.expiration}")
    private long expiration;
    @Value("${spring.security.jwt.refresh-expiration}")
    private long refreshExpiration;


    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    public boolean isExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }

    private Date extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration);
    }

    private <T> T extractClaim(String jwt, Function<Claims, T> function) {
        return function.apply(extractAllClaims(jwt));
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parse(jwt)
                .accept(Jws.CLAIMS)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    }

    public String createJwt(User user) {
        return generateJwt(user);
    }

    private String generateJwt(User user) {
        return generateJwt(user, new HashMap<>());
    }

    private String generateJwt(User user, Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey(), Jwts.SIG.HS512)
                .compact();
    }
}
