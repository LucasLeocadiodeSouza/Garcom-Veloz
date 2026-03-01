package com.back.demo.exception;

public class PerfilNotFoundException extends RuntimeException{
    public PerfilNotFoundException(String msn){
        super(msn);
    }
}
