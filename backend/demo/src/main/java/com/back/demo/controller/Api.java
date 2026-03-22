package com.back.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import com.back.demo.model.Categoria;
import com.back.demo.model.CategoriaDTO;
import com.back.demo.model.EstatisticasDTO;
import com.back.demo.model.FormTela;
import com.back.demo.model.Item;
import com.back.demo.model.ItemDTO;
import com.back.demo.model.ItemMedia;
import com.back.demo.model.PedidoDTO;
import com.back.demo.model.PedidoItemDTO;
import com.back.demo.service.GenSvc;
import com.back.demo.service.ItemSvc;
import com.back.demo.service.PedidoSvc;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.nio.file.Path;


@RestController
@RequestMapping("/api")
public class Api {

    @Autowired
    private ItemSvc itemService;

    @Autowired
    private PedidoSvc pedidoSvc;

    @Autowired
    private GenSvc genService;



    @GetMapping("/teste")
    public String getTeste(){
        return "teste";
    }

    private String mediaDirectory = System.getProperty("user.dir") + "/media/itens";

    @GetMapping("/validarAutenticacao")
    private Boolean validarAutenticacao(HttpServletRequest request) {
        return genService.validarAutenticacao(request);
    }

    // ####################### ITENS #######################

    @GetMapping("/getStatsHome")
    private EstatisticasDTO getStatsHome(){
        return genService.getStatsHome();
    }

    @GetMapping("/getFormTelas")
    private List<FormTela> getFormTelas(HttpServletRequest request){
        return genService.getFormTelas();
    }

    @GetMapping("/getTelasByPerfil")
    private List<FormTela> getTelasByPerfil(HttpServletRequest request){
        return genService.getFormTelasByPerfilUsu(genService.getUserName(request));
    }

    // ####################### CATEGORIAS #######################

    // @GetMapping("/getStatsCategoria")
    // public CategoriaDTO getStatsCategoria(@RequestParam(name = "search", required = false) String search, @RequestParam(value = "status", required = false) String status){
    //     return itemService.getStatsCategoria(status, search);
    // }

    @GetMapping("/getCategoriaGrid")
    private List<CategoriaDTO> getCategoriaGrid(@RequestParam(name = "search", required = false) String search, @RequestParam(value = "status", required = false) String status){
        return itemService.getListCategoria(search, status);
    }

    @GetMapping("/getAllCategoria")
    private List<Categoria> getAllCategoria(){
        return itemService.getAllCategoriaActives();
    }

    @PostMapping("/ativarInativarCategoria")
    private ResponseEntity<?> ativarInativarCategoria(@RequestParam(name = "idCategoria", required = true) Long idCategoria, 
                                                      @RequestParam(value = "ativar", required = true) Boolean ativar,
                                                      HttpServletRequest request){
        itemService.ativarInativarCategoria(idCategoria, ativar, genService.getUserName(request));
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Status da categoria alterado com sucesso"
        ));
    }

    @PostMapping("/criarAlterarCategoria")
    private ResponseEntity<?> criarAlterarCategoria(@RequestBody CategoriaDTO dto, HttpServletRequest request){
        itemService.criarAlterarCategoria(dto.getIdCategoria(), dto.getDescricao(), dto.getIcone(), dto.getCor(), null, genService.getUserName(request));
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Categoria Criada/Alterada com sucesso"));
    }

    @PostMapping("/excluirCategoria")
    private ResponseEntity<?> excluirCategoria(@RequestParam(name = "idCategoria", required = true) Long idCategoria, HttpServletRequest request ){
        itemService.excluirCategoria(idCategoria, genService.getUserName(request));
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Categoria excluida com sucesso"
        ));
    }

    // ####################### ITENS #######################

    @GetMapping("/getItensGrid")
    private List<ItemDTO> getItensGrid(@RequestParam(name  = "search", required = false)      String search, 
                                       @RequestParam(value = "idCategoria", required = false) Long idCategoria, 
                                       @RequestParam(value = "status", required = false)      String status){
        return itemService.getListItem(search, status, idCategoria);
    }

    @GetMapping("/getItemInfo")
    private Item getItemInfo(@RequestParam(name  = "idItem", required = true) Long idItem){
        return itemService.getItemInfo(idItem);
    }

    @PostMapping("/criarAlterarItem")
    private ResponseEntity<?> criarAlterarItem(@RequestBody ItemDTO dto, HttpServletRequest request){
        itemService.criarAlterarItem(dto.getIdItem(), 
                                     dto.getNome(), 
                                     dto.getDescricao(), 
                                     dto.getValor(), 
                                     dto.getDesconto(), 
                                     dto.getEstoque(), 
                                     dto.getIdCategoria(),
                                     null,
                                     genService.getUserName(request));
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Item Criado/Alterado com sucesso"
        ));
    }

    @PostMapping("/ativarInativarItem")
    private ResponseEntity<?> ativarInativarItem(@RequestParam(name = "idItem", required = true) Long itemId, 
                                                 @RequestParam(name = "ativar", required = true) Boolean ativar,
                                                 HttpServletRequest request){
        itemService.ativarInativarItem(itemId, ativar, genService.getUserName(request));

        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Item ativado/desativado com sucesso"
        ));
    }

    @PostMapping("/excluiItem")
    private ResponseEntity<?> excluiItem(@RequestParam(name = "idItem", required = true) Long itemId, HttpServletRequest request){
        itemService.excluiItem(itemId, genService.getUserName(request));

        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Item excluido com sucesso"
        ));
    }

    @PostMapping(value = "/registrarMediaProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    private ResponseEntity<?> registrarMediaProduct(@RequestParam(value = "idItem", required = false)   Long idItem,
                                                    @RequestParam(value = "images[]",  required = false) MultipartFile[] images,
                                                    HttpServletRequest request ) throws IOException{

        itemService.adapterVincularItemImage(images, idItem, genService.getUserName(request));

        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Media adicionada com sucesso"
        ));
    }

    @GetMapping("/getListMediaItem")
    private List<ItemMedia> getListMediaItem(@RequestParam(name  = "idItem", required = false) Long idItem){
        return itemService.getListMediaItem(idItem);
    }

    @GetMapping("/mediaItem/{filename}")
    private ResponseEntity<Resource> getMediaImage(@PathVariable String filename) {
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

    // ####################### PEDIDOS #######################

    @GetMapping("/getListPedidos")
    private List<PedidoDTO> getListPedidos(@RequestParam(name = "mesa", required = false)       Integer mesa,
                                           @RequestParam(name = "estado", required = false)     Integer estado,
                                           @RequestParam(name = "dataInicio", required = false) String dataInicio,
                                           @RequestParam(name = "dataFim", required = false)    String dataFim){

        LocalDate inicio = (dataInicio != null && !dataInicio.isEmpty()) ? LocalDate.parse(dataInicio) : null;
        LocalDate fim = (dataFim != null && !dataFim.isEmpty()) ? LocalDate.parse(dataFim) : null;

        return pedidoSvc.getListPedidosDTO(mesa, estado, inicio, fim);
    }

    @GetMapping("/getListPedidosItem")
    private List<PedidoItemDTO> getListPedidosItem(@RequestParam(name = "idPedido", required = true) Long idPedido) {
        return pedidoSvc.getListPedidosItemDTO(idPedido);
    }

    @PostMapping("/criarAlterarPedido")
    private ResponseEntity<?> criarAlterarPedido(@RequestBody PedidoDTO dto, HttpServletRequest request){
        String ideusu = genService.getUserName(request);

        pedidoSvc.criarAlterarPedido(dto.getId(), 
                                     dto.getObservacao(), 
                                     dto.getGorgeta(), 
                                     dto.getMesa(), 
                                     genService.getCodEmpresaByIdeusu(ideusu), 
                                     ideusu);

        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Pedido Criado/Alterado com sucesso"
        ));
    }

    @PostMapping("/alterarEstadoPedido")
    private ResponseEntity<?> alterarEstadoPedido(@RequestParam(name = "idPedido", required = true) Long    idPedido,
                                                  @RequestParam(name = "estado", required = true)   Integer estado,
                                                  HttpServletRequest request){
        pedidoSvc.alterarEstadoPedido(idPedido, 
                                      estado,
                                      genService.getUserName(request));
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Alterado situação do Pedido com sucesso"
        ));
    }

    @PostMapping("/criarAlterarItemPedido")
    private ResponseEntity<?> criarAlterarItemPedido(@RequestParam(name = "idPedido", required = true)   Long idPedido,
                                                     @RequestParam(name = "idItem", required = true)     Long idItem,
                                                     @RequestParam(name = "quantidade", required = true) Integer quantidade,
                                                     @RequestParam(name = "seq", required = true)        Long seq,
                                                     HttpServletRequest request){
        pedidoSvc.criarAlterarItemPedido(idPedido, 
                                        idItem,
                                        seq,
                                        quantidade,
                                        genService.getUserName(request));
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Item do Pedido Criado/Alterado com sucesso"
        ));
    }

    @PostMapping("/vinculaItemPedido")
    private ResponseEntity<?> vinculaItemPedido(@RequestParam(name = "idPedido", required = true)   Long idPedido,
                                                @RequestParam(name = "idItem", required = true)     Long idItem,
                                                @RequestParam(name = "quantidade", required = true) Integer quantidade,
                                                @RequestParam(name = "seq", required = true)        Long seq,
                                                HttpServletRequest request){
        pedidoSvc.vinculaItemPedido(idPedido, 
                                    idItem,
                                    seq,
                                    quantidade,
                                    genService.getUserName(request));
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Item do Pedido Vinculado com sucesso"
        ));
    }

    @PostMapping("/excluirItemPedido")
    private ResponseEntity<?> excluirItemPedido(@RequestParam(name = "idPedido", required = true)   Long idPedido,
                                                @RequestParam(name = "idItem", required = true)     Long idItem,
                                                @RequestParam(name = "seq", required = true)        Long seq,
                                                HttpServletRequest request){
        pedidoSvc.excluiItemPedido(idPedido, 
                                   idItem,
                                   seq,
                                   genService.getUserName(request));
        
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Item do Pedido excluido com sucesso"));
    }

    @PostMapping("/alterarEstadoItemPedido")
    private ResponseEntity<?> alterarEstadoItemPedido(@RequestParam(name = "idPedido", required = true)   Long    idPedido,
                                                      @RequestParam(name = "idItem", required = true)     Long    idItem,
                                                      @RequestParam(name = "seq", required = true)        Long    seq,
                                                      @RequestParam(name = "estado", required = true)     Integer estado,
                                                      HttpServletRequest request){
        pedidoSvc.alterarEstadoItemPedido(idPedido, 
                                          idItem,
                                          seq,
                                          estado,
                                          genService.getUserName(request));
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Alterado situação do Item do Pedido com sucesso"
        ));
    }
}