package com.noix.spendtracker.bank;

import com.noix.spendtracker.bank.token.Bank;
import com.noix.spendtracker.user.User;
import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Mono;

import javax.crypto.AEADBadTagException;

public interface BankService {

    Mono<? extends ClientDTO> fetchClientData(User user) throws AEADBadTagException;



}
