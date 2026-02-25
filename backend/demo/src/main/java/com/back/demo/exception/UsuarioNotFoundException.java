package com.back.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UsuarioNotFoundException extends RuntimeException {

    public UsuarioNotFoundException(Long id) {
        super("Usuário não encontrado com id: " + id);
    }

    public UsuarioNotFoundException(String field){
        super("Usuário não encontrado com o username " + field);
    }
}
