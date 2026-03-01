package com.back.demo.exception;

public class RestricaoException extends RuntimeException {
    public RestricaoException(String msn){
        super(msn);
    }
}
