package com.back.demo.exception;

public class RestricaoNotFoundException extends RuntimeException{
    public RestricaoNotFoundException(String msn){
        super(msn);
    }
}
