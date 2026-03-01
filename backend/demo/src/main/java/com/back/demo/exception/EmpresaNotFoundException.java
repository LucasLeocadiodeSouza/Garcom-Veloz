package com.back.demo.exception;

public class EmpresaNotFoundException extends RuntimeException{
    public EmpresaNotFoundException(String msn){
        super(msn);
    }
}
