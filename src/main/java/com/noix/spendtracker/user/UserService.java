package com.noix.spendtracker.user;

import com.noix.spendtracker.exception.UsernameTakenException;
import com.noix.spendtracker.user.role.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new NullPointerException("Username not valid: " + username);
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Username: " + username));
    }

    public User createUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            return User.empty();
        } else {
            User user = userRepository.save(
                    new User(
                            username,
                            password,
                            null,
                            Role.USER,
                            true
                    )
            );
            return user;
        }
    }

    public boolean validateUser(User user) {
        if (user == null || user.isEmpty()) return false;
        Optional<User> optional = userRepository.findByUsername(user.getUsername());
        if (optional.isEmpty()) return false;
        return optional.get().equals(user);
    }
}
