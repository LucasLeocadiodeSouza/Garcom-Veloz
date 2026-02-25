package com.back.demo.exception;

public class PedidoException extends RuntimeException{
    public PedidoException(String msn) {
        super(msn);
    }
}
