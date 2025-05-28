package com.noix.spendtracker.bank.token;

import com.noix.spendtracker.security.aes.AESService;
import com.noix.spendtracker.user.User;
import com.noix.spendtracker.user.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.AEADBadTagException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiTokenService {

    private static final Logger logger = LoggerFactory.getLogger(ApiTokenService.class);

    private final ApiTokenRepository tokenRepository;
    private final AESService aesService;
    private final UserService userService;

    public String getTokenForUser(User user, Bank bank) throws AEADBadTagException {
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User is empty");
        } else if (bank == null) {
            throw new IllegalArgumentException("Bank is null");
        }

        Optional<ApiToken> opToken = tokenRepository.findByUserAndBank(user, bank);
        if (opToken.isEmpty()) {
            throw new EntityNotFoundException("No tokens found for user.id: " + user.getId() + " and bank: " + bank.name());
        }
        return aesService.decrypt(opToken.get().getKey()); //in case of AEADBadTag logger.error("GCM authentication tag does not match the calculated value")
    }

    public void save(User user, String key, Bank bank) {
        if (key == null || key.isEmpty()) throw new IllegalArgumentException("Api token not present");
        if (bank == null) throw new IllegalArgumentException("Bank is null");
        if (!userService.validateUser(user)) throw new IllegalArgumentException("User is not valid, id: " + user.getId());

        final String encoded = aesService.encrypt(key);
        if(tokenRepository.existsByKey(encoded)) {
            logger.debug("Key already stored in db");
            return;
        }

        ApiToken token = ApiToken.builder()
                .key(encoded)
                .bank(bank)
                .user(user)
                .build();

        tokenRepository.save(token);
    }

    public void delete(ApiToken token) {
        //todo: implement
    }
}