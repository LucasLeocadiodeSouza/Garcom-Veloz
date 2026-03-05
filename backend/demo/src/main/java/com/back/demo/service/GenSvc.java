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

    public String[] getAllCorCategoria(){
        return new String[]{"#3b82f6", "#10b981", "#f59e0b", "#ef4444", "#8b5cf6", "#ec4899", "#14b8a6", "#f97316"};
    }

    public String[] getAllIconeCategoria(){
        return new String[]{"🍽️", "🥤", "🍔", "🍕", "🥗", "🍰", "🍷", "🥩", "🌮", "🍜", "🍣", "🧃"};
    }
}
