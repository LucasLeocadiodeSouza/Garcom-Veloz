package com.back.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.back.demo.model.CategoriaDTO;
import com.back.demo.service.ItemSvc;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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


    // ####################### CATEGORIAS #######################

    // @GetMapping("/getStatsCategoria")
    // public CategoriaDTO getStatsCategoria(@RequestParam(name = "search", required = false) String search, @RequestParam(value = "status", required = false) String status){
    //     return itemService.getStatsCategoria(status, search);
    // }

    @GetMapping("/getCategoriaGrid")
    public List<CategoriaDTO> getCategoriaGrid(@RequestParam(name = "search", required = false) String search, @RequestParam(value = "status", required = false) String status){
        return itemService.getListCategoria(search, status);
    }

    @PostMapping("/ativarInativarCategoria")
    public ResponseEntity<?> ativarInativarCategoria(@RequestParam(name = "idCategoria", required = true) Long idCategoria, @RequestParam(value = "ativar", required = true) Boolean ativar){
        itemService.ativarInativarCategoria(idCategoria, ativar);
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Status da categoria alterado com sucesso"
        ));
    }

    @PostMapping("/criarAlterarCategoria")
    public ResponseEntity<?> criarAlterarCategoria(@RequestBody CategoriaDTO dto){
        itemService.criarAlterarCategoria(dto.getIdCategoria(), dto.getDescricao(), dto.getIcone(), dto.getCor(), null, "LUCASSZ");
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Categoria Criada/Alterada com sucesso"
        ));
    }

    @PostMapping("/excluirCategoria")
    public ResponseEntity<?> excluirCategoria(@RequestParam(name = "idCategoria", required = true) Long idCategoria){
        itemService.excluirCategoria(idCategoria, "LUCASSZ");
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Categoria excluida com sucesso"
        ));
    }



    


}