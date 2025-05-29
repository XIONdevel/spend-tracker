package com.noix.spendtracker.bank.mono;

import com.noix.spendtracker.bank.BankService;
import com.noix.spendtracker.bank.token.ApiTokenService;
import com.noix.spendtracker.bank.token.Bank;
import com.noix.spendtracker.user.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.AEADBadTagException;

@Service
@RequiredArgsConstructor
public class MonoService implements BankService {

    private static final WebClient webClient = WebClient.create("https://api.monobank.ua/");
    private static final Bank BANK = Bank.MONO;

    private final ApiTokenService apiService;


    public Mono<MonoClientDTO> fetchClientData(User user) throws AEADBadTagException {
        final String token = apiService.getTokenForUser(user, BANK);

        return webClient.get()
                .uri("personal/client-info")
                .header("X-Token", token)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException(//todo: replace with readable code
                                        String.format("API returned an error: Status %s, Body: %s",
                                                clientResponse.statusCode(), errorBody)))))
                .bodyToMono(MonoClientDTO.class);
    }


}
