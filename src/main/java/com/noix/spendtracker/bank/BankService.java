package com.noix.spendtracker.bank;

import com.noix.spendtracker.user.User;
import reactor.core.publisher.Mono;

import javax.crypto.AEADBadTagException;

public interface BankService {

    Mono<? extends ClientDTO> fetchClientInfo(User user) throws AEADBadTagException;



}
