package com.noix.spendtracker.security.authentication;

import com.noix.spendtracker.security.jwt.JwtService;
import com.noix.spendtracker.security.token.RefreshToken;
import com.noix.spendtracker.security.token.RefreshTokenService;
import com.noix.spendtracker.user.User;
import com.noix.spendtracker.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshService;

    public void authenticate(AuthenticationRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User user = userService.loadUserByUsername(request.getUsername());
        String jwt = jwtService.createJwt(user);
        RefreshToken refreshToken = jwtService.createToken(user);

        Cookie cookie = new Cookie("token", refreshToken.getJwt());
        cookie.setMaxAge(refreshToken.getExpiresAt().getSeconds());

        response.addCookie(cookie);
        response.addHeader("Authorization", "Bearer " + jwt);
    }

    //todo: optionally add password complexity check
    public void register(AuthenticationRequest request, HttpServletResponse response) throws IOException {
        User user = userService.createUser(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword())
        );
        if (user.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_CONFLICT); //409
            response.getWriter().write("Username is taken"); //todo: replace with smthing
            return;
        }
        String jwt = jwtService.createJwt(user);
        RefreshToken refreshToken = jwtService.createToken(user);

        Cookie cookie = new Cookie("token", refreshToken.getJwt());
        cookie.setMaxAge(refreshToken.getExpiresAt().getSeconds());

        response.addCookie(cookie);
        response.addHeader("Authorization", "Bearer " + jwt);
    }

    public void logoutAll(HttpServletRequest request, HttpServletResponse response) {
        User user = refreshService.extractToken(request).getUser();
        refreshService.deleteAllByUser(user);
        final Cookie c = new Cookie("token", "");
        c.setMaxAge(1);
        response.addCookie(c);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        refreshService.deleteToken(refreshService.extractToken(request).getJwt());
        final Cookie c = new Cookie("token", "");
        c.setMaxAge(1);
        response.addCookie(c);
    }
}
