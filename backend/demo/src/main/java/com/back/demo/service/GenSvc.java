package com.back.demo.service;

import org.springframework.stereotype.Service;

@Service
public class GenSvc {

    public String formataTelefoneBanco(String telefone){
        if(telefone.contains("(")) telefone = telefone.replace("(", "");
        if(telefone.contains(")")) telefone = telefone.replace(")", "");
        if(telefone.contains(" ")) telefone = telefone.replace(" ", "");
        if(telefone.contains("-")) telefone = telefone.replace("-", "");

        return telefone;
    }

    
}
