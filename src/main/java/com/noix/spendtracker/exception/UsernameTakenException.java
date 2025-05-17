package com.noix.spendtracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UsernameTakenException extends RuntimeException {

    public UsernameTakenException(String message) {
        super(message);
    }

}
