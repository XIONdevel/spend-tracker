package com.noix.spendtracker.bank.mono;

import com.noix.spendtracker.bank.BankService;
import com.noix.spendtracker.bank.token.ApiTokenService;
import com.noix.spendtracker.bank.token.Bank;
import com.noix.spendtracker.exception.ThirdPartyApiException;
import com.noix.spendtracker.user.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatusCode;
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

    //todo: add detailed status handling
    public Mono<MonoClientDTO> fetchClientData(User user) throws AEADBadTagException {
        final String token = apiService.getTokenForUser(user, BANK);

        return webClient.get()
                .uri("personal/client-info")
                .header("X-Token", token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new BadRequestException(errorBody))))
                .onStatus(HttpStatusCode::is5xxServerError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new ThirdPartyApiException(errorBody))))
                .bodyToMono(MonoClientDTO.class);
    }


}
