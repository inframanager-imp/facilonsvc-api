package com.facilon.app.module.serviceagent.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class DelegationExpiredException extends RuntimeException {
    public DelegationExpiredException(String message) {
        super(message);
    }
}
