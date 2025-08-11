package com.noix.spendtracker.bank;

import com.noix.spendtracker.bank.mono.MonoService;
import com.noix.spendtracker.bank.token.Bank;
import com.noix.spendtracker.util.CommonUtils;
import com.noix.spendtracker.security.jwt.JwtService;
import com.noix.spendtracker.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.AEADBadTagException;
import java.util.Map;
import java.util.TreeMap;

@Service
public class GlobalBankingService {

    private static final Logger logger = LoggerFactory.getLogger(GlobalBankingService.class);
    private final JwtService jwtService;
    private final Map<Bank, BankService> services;

    @Autowired
    public GlobalBankingService(JwtService jwtService, MonoService monoService) {
        services = new TreeMap<>();
        this.jwtService = jwtService;

        services.put(Bank.MONO, monoService);
    }

    public Mono<? extends ClientDTO> fetchClientInfo(HttpServletRequest request, Bank bank) {
        User user = jwtService.extractUser(request);
        if (user.isEmpty()) throw new EntityNotFoundException("User not found");

        try {
            return services.get(bank).fetchClientInfo(user);
        } catch (AEADBadTagException e) {
            String message = String.format(
                    "GCM authentication tag does not match the calculated value. User.id:%d, requester.ip:%s",
                    user.getId(), CommonUtils.getRequestIp(request)
            );
            logger.error(message);
            throw new RuntimeException(e);
        }
    }


}
