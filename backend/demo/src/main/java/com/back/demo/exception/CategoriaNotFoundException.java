package com.back.demo.exception;

public class CategoriaNotFoundException extends RuntimeException{
    public CategoriaNotFoundException(String msn){
        super(msn);
    }
}
