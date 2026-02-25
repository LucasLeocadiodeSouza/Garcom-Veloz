package com.back.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LoginNotFoundException extends RuntimeException {

    public LoginNotFoundException(String name) {
        super("Login não encontrado para o usuário: " + name);
    }

    public LoginNotFoundException(String name, String message) {
        super(message);
    }
}
