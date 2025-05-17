package com.noix.spendtracker.security.token;

import com.noix.spendtracker.user.User;
import com.noix.spendtracker.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final UserService userService;

    public boolean isValid(String jwt, User user) {


        return false;
    }
}
