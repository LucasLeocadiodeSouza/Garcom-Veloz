package com.back.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.back.demo.model.CategoriaDTO;
import com.back.demo.service.ItemSvc;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
public class Api {
    
    @Autowired
    private ItemSvc itemService;

    @GetMapping("/teste")
    public String getTeste(){
        return "teste";
    }

    @GetMapping("/getStatsCategoria")
    public CategoriaDTO getStatsCategoria(@RequestParam(name = "search", required = false) String search, @RequestParam(value = "status", required = false) String status){
        return itemService.getStatsCategoria(status, search);
    }

    @GetMapping("/getCategoriaGrid")
    public List<CategoriaDTO> getCategoriaGrid(@RequestParam(name = "search", required = false) String search, @RequestParam(value = "status", required = false) String status){
        return itemService.getListCategoria(search, status);
    }

}