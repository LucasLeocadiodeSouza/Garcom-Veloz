package com.back.demo.exception;

public class UsuarioException extends RuntimeException{
    public UsuarioException(String msn){
        super(msn);
    }
}
