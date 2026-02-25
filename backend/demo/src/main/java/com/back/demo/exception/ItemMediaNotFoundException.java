package com.back.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ItemMediaNotFoundException extends RuntimeException {

    public ItemMediaNotFoundException(Long idItem, Integer seq) {
        super("ItemMedia não encontrada para id_item: " + idItem + " e seq: " + seq);
    }

    public ItemMediaNotFoundException(String message) {
        super(message);
    }
}
