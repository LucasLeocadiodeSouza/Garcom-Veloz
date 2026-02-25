package com.back.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PedidoItemNotFoundException extends RuntimeException {

    public PedidoItemNotFoundException(Long idPedido, Long idItem) {
        super("PedidoItem não encontrado para id_pedido: " + idPedido + " e id_item: " + idItem);
    }

    public PedidoItemNotFoundException(String message) {
        super(message);
    }
}
