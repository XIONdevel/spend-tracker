package com.noix.spendtracker.security.authentication;

import com.noix.spendtracker.exception.UsernameTakenException;
import com.noix.spendtracker.security.jwt.JwtService;
import com.noix.spendtracker.security.token.Token;
import com.noix.spendtracker.security.token.TokenService;
import com.noix.spendtracker.user.User;
import com.noix.spendtracker.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Calendar;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;


    public void authenticate(AuthenticationRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User user = userService.loadUserByUsername(request.getUsername());
        String jwt = jwtService.createJwt(user);
        Token token = jwtService.createToken(user);

        Cookie cookie = new Cookie("token", token.getJwt());
        cookie.setMaxAge(token.getExpiresAt().getSeconds());

        response.addCookie(cookie);
        response.addHeader("Authorization", "Bearer " + jwt);
    }

    //todo: optionally add password complexity check
    public void register(AuthenticationRequest request, HttpServletResponse response) throws IOException {
        Optional<User> optionalUser = userService.createUser(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword())
        );
        if (optionalUser.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_CONFLICT); //409
            response.getWriter().write("Username is taken"); //todo: replace with smthing
            return;
        }
        User user = optionalUser.get();
        String jwt = jwtService.createJwt(user);
        Token token = jwtService.createToken(user);

        Cookie cookie = new Cookie("token", token.getJwt());
        cookie.setMaxAge(token.getExpiresAt().getSeconds());

        response.addCookie(cookie);
        response.addHeader("Authorization", "Bearer " + jwt);
    }

}
