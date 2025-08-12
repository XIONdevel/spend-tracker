package com.noix.spendtracker.security.jwt;

import com.noix.spendtracker.security.token.RefreshToken;
import com.noix.spendtracker.security.token.RefreshTokenService;
import com.noix.spendtracker.user.User;
import com.noix.spendtracker.user.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final String SECRET;
    private final long jwtExpiration;
    private final long refreshExpiration;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    @Autowired
    public JwtService(RefreshTokenService refreshTokenService,
                      UserService userService,
                      @Value("${spring.security.jwt.refresh-expiration}")long refreshExpiration,
                      @Value("${spring.security.jwt.jwt-expiration}")long jwtExpiration,
                      @Value("${spring.security.jwt.secret}")String SECRET) {
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
        this.refreshExpiration = refreshExpiration;
        this.jwtExpiration = jwtExpiration;
        this.SECRET = SECRET;
    }

    public User extractUser(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return User.empty();
        return extractUser(header.substring(7));
    }

    public User extractUser(String jwt) {
        if (isExpired(jwt)) return User.empty();
        String username = extractUsername(jwt);
        return userService.loadUserByUsername(username);
    }

    public RefreshToken createToken(User user) {
        String jwt = generateJwt(user, refreshExpiration);
        Date exp = extractExpiration(jwt);
        return refreshTokenService.createToken(user, jwt, exp);
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
        return isExpired(jwt) && refreshTokenService.validateToken(jwt, user);
    }

    public boolean validateJwt(String jwt, User user) {
        User test = userService.loadUserByUsername(user.getUsername());
        return !isExpired(jwt) && test.equals(user);
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