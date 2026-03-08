package com.back.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import com.back.demo.model.Categoria;
import com.back.demo.model.CategoriaDTO;
import com.back.demo.model.ItemDTO;
import com.back.demo.model.ItemMedia;
import com.back.demo.service.ItemSvc;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.nio.file.Files;
import java.nio.file.Path;


@RestController
@RequestMapping("/api")
public class Api {
    
    @Autowired
    private ItemSvc itemService;

    @GetMapping("/teste")
    public String getTeste(){
        return "teste";
    }

    private String mediaDirectory = System.getProperty("user.dir") + "/media/itens";

    // ####################### CATEGORIAS #######################

    // @GetMapping("/getStatsCategoria")
    // public CategoriaDTO getStatsCategoria(@RequestParam(name = "search", required = false) String search, @RequestParam(value = "status", required = false) String status){
    //     return itemService.getStatsCategoria(status, search);
    // }

    @GetMapping("/getCategoriaGrid")
    public List<CategoriaDTO> getCategoriaGrid(@RequestParam(name = "search", required = false) String search, @RequestParam(value = "status", required = false) String status){
        return itemService.getListCategoria(search, status);
    }

    @GetMapping("/getAllCategoria")
    public List<Categoria> getAllCategoria(){
        return itemService.getAllCategoriaActives();
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

    // ####################### ITENS #######################

    @GetMapping("/getItensGrid")
    public List<ItemDTO> getItensGrid(@RequestParam(name  = "search", required = false)      String search, 
                                      @RequestParam(value = "idCategoria", required = false) Long idCategoria, 
                                      @RequestParam(value = "status", required = false)      String status){
        return itemService.getListItem(search, status, idCategoria);
    }

    @PostMapping("/criarAlterarItem")
    public ResponseEntity<?> criarAlterarItem(@RequestBody ItemDTO dto){
        itemService.criarAlterarItem(dto.getIdItem(), dto.getNome(), dto.getDescricao(), dto.getValor(), dto.getDesconto(), dto.getEstoque(), dto.getIdCategoria(), null, "LUCASSZ");
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Item Criado/Alterado com sucesso"
        ));
    }

    @PostMapping("/ativarInativarItem")
    public ResponseEntity<?> ativarInativarItem(@RequestParam(name = "idItem", required = true) Long itemId, @RequestParam(name = "ativar", required = true) Boolean ativar){
        itemService.ativarInativarItem(itemId, ativar, "LUCASSZ");
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Item ativado/desativado com sucesso"
        ));
    }

    @PostMapping("/excluiItem")
    public ResponseEntity<?> excluiItem(@RequestParam(name = "idItem", required = true) Long itemId){
        itemService.excluiItem(itemId, "LUCASSZ");
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Item excluido com sucesso"
        ));
    }

    @PostMapping(value = "/registrarMediaProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registrarMediaProduct(@RequestParam(value = "idItem", required = false)    Long idItem,
                                                   @RequestParam(value = "images[]",  required = false) MultipartFile[] images) throws IOException{

        itemService.adapterVincularItemImage(images, idItem, "LUCASSZ");
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Media adicionada com sucesso"
        ));
    }

    @GetMapping("/getListMediaItem")
    public List<ItemMedia> getListMediaItem(@RequestParam(name  = "idItem", required = false) Long idItem){
        return itemService.getListMediaItem(idItem);
    }

    @GetMapping("/mediaItem/{filename}")
    public ResponseEntity<Resource> getMediaImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(mediaDirectory).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}