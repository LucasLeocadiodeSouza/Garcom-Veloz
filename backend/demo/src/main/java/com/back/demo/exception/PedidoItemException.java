package com.back.demo.exception;

public class PedidoItemException extends RuntimeException {
    public PedidoItemException(String msn){
        super(msn);
    }
}
