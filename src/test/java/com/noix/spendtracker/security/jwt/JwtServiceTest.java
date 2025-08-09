package com.noix.spendtracker.security.jwt;

import com.noix.spendtracker.security.token.RefreshTokenService;
import com.noix.spendtracker.user.User;
import com.noix.spendtracker.user.UserService;
import com.noix.spendtracker.user.role.Role;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    @Mock
    static RefreshTokenService refreshTokenService;
    @Mock
    static UserService userService;

    static JwtService jwtService;

    @BeforeAll
    static void beforeAll() {
        String secret = "M6Frc4ucnWmqM0LoPKruoWX6W8auInGSx66BFVhHGPqtlfxB5tkseU/mPD0jttEBv9LL7xN7qqoaHY5yNc+TVA==";
        long exp = 1000L * 60 * 60; //1h
        long refreshExp = exp * 24 * 30;
        jwtService = new JwtService(refreshTokenService, userService, refreshExp, exp, secret);
    }

    @Test
    void isExpired() {
        User user = new User(1L, "username", "pass", "email", Role.USER, true);
        String jwt = jwtService.createJwt(user);
        assertThat(jwtService.isExpired(jwt)).isFalse();
    }
}