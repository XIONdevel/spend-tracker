package com.noix.spendtracker.bank;

import com.noix.spendtracker.bank.token.Bank;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/bank")
@RequiredArgsConstructor
public class BankController {

    private final GlobalBankingService bankingService;

    @PostMapping("/personal/client-info")
    public Mono<? extends ClientDTO> getClientInfo(HttpServletRequest request, @RequestBody Bank bank) {
        return bankingService.fetchClientInfo(request, bank);
    }

    @DeleteMapping("/placeholder")
    public ResponseEntity<?> removeApiToken(HttpServletRequest request, @RequestBody Bank bank) {
        return null;
        //todo: implement
    }



}
