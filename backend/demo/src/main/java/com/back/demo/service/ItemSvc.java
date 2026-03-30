package com.back.demo.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.back.demo.exception.CategoriaException;
import com.back.demo.exception.CategoriaNotFoundException;
import com.back.demo.exception.ItemException;
import com.back.demo.model.Categoria;
import com.back.demo.model.CategoriaDTO;
import com.back.demo.model.Item;
import com.back.demo.model.ItemDTO;
import com.back.demo.model.ItemMedia;
import com.back.demo.model.ItemMediaId;
import com.back.demo.repository.CategoriaDTORepository;
import com.back.demo.repository.CategoriaRepository;
import com.back.demo.repository.ItemDTORepository;
import com.back.demo.repository.ItemMediaRepository;
import com.back.demo.repository.ItemRepository;
import jakarta.transaction.Transactional;

@Service
public class ItemSvc {

    @Autowired
    private ItemRepository itemRepo;

    @Autowired
    private ItemMediaRepository itemImgRepo;

    @Autowired
    private CategoriaRepository categoriaRepo;

    @Autowired
    private CategoriaDTORepository categoriaDTORepo;

    @Autowired
    private UserSvc userSvc;

    @Autowired
    private ItemDTORepository itemDTORepo;

    @Autowired
    private GenSvc genSvc; 

    private String itensDirectory = System.getProperty("user.dir") + "/media/itens";


    public List<CategoriaDTO> getListCategoria(String descricao, String ativo){
        List<CategoriaDTO> categorias = categoriaDTORepo.getCategoriaList(ativo, descricao);

        return categorias;
    }

    public List<Categoria> getAllCategoriaActives(){
        List<Categoria> categorias = categoriaRepo.findAllCategoriaByStatus(true);

        return categorias;
    }

    public Long getCountAllCategorias(){
        Long categorias = categoriaRepo.countAllCategoria();
        return categorias;
    }

    public Long getCountAllCategoriasAtivos(){
        Long categorias = categoriaRepo.countAllCategoriaByStatus(true);
        return categorias;
    }

    public CategoriaDTO getStatsCategoria(String status, String search){
        CategoriaDTO categoria = categoriaDTORepo.getStatsCategoria(status, search);

        return categoria;
    }

    public Item getItemInfo(Long idItem){
        Item item = itemRepo.findItemById(idItem);
        return item;
    }

    // CRIAR, ALTERAR e EXCLUIR os itens

    @Transactional
    public void criarAlterarCategoria(Long       id,
                                      String     descricao,
                                      String     icone,
                                      String     cor,
                                      Long       referencia_ext,
                                      String     ideusu){
        Boolean isNewCateg = false;

        genSvc.validaUsuarioByIdeusu(ideusu);     

        if(descricao == null || descricao.isBlank()) throw new CategoriaException("É preciso informar a descrição do item!");
        
        genSvc.usuarioTemPermissao("Editar Categoria", ideusu);

        if(id != null){
            Categoria categoriaNome = categoriaRepo.findCategoriaByDescricao(descricao);
            if(categoriaNome != null) throw new CategoriaException("Categoria '" + categoriaNome.getDescricao() + "' já está vinculada [código '" + categoriaNome.getId() + "'].");
        }
        
        Categoria categoria = categoriaRepo.findCategoriaById(id);

        if(categoria == null){
            categoria = new Categoria();
            categoria.setIdeusu(ideusu);
            categoria.setCriadoEm(LocalDate.now());
            categoria.setAtivo(true);

            isNewCateg = true;
        }

        categoria.setDescricao(descricao);
        categoria.setCor(cor);
        categoria.setIcone(icone);
        categoria.setRefereciaExt(referencia_ext);

        categoriaRepo.save(categoria);

        userSvc.salvarHistoricoUsuario("Categoria '" + descricao + "' " + (isNewCateg? "Cadastrado" : "Atualizado"), ideusu);
    }

    @Transactional
    public void ativarInativarCategoria(Long id, Boolean ativar, String ideusu){
        genSvc.validaUsuarioByIdeusu(ideusu);

        genSvc.usuarioTemPermissao("Editar Categoria", ideusu);

        Categoria categoria = categoriaRepo.findCategoriaById(id);
        if(categoria == null) throw new CategoriaNotFoundException("Não encontrado a Categoria");

        categoria.setAtivo(ativar);

        categoriaRepo.save(categoria);

        userSvc.salvarHistoricoUsuario("Categoria '" + categoria.getDescricao() + "' " + (ativar? "Ativada" : "Desativada"), ideusu);
    }

    @Transactional
    public void excluirCategoria(Long id, String ideusu){
        String descCateg = "";

        genSvc.validaUsuarioByIdeusu(ideusu);

        genSvc.usuarioTemPermissao("Excluir Categoria", ideusu);

        Categoria categoria = categoriaRepo.findCategoriaById(id);
        if(categoria == null) throw new CategoriaNotFoundException("Não encontrado a Categoria");

        Long countProdutosVinculados = categoriaDTORepo.getCountProductByCategoria(categoria.getId(), null, null);
        if(countProdutosVinculados >= 1) throw new CategoriaException("Categoria possui itens vinculados, altere a categoria dos itens para excluir a categoria selecionada");

        descCateg = categoria.getDescricao();

        categoriaRepo.delete(categoria);

        userSvc.salvarHistoricoUsuario("Categoria '" + descCateg + "' excluida", ideusu);
    }




    public List<ItemDTO> getListItem(String nome, String ativo, Long categoriaId){
        List<ItemDTO> itens = itemDTORepo.getListItem(nome, ativo, categoriaId);

        return itens;
    }

    
    public List<ItemDTO> getItensForCardapio(String nome, String ativo, Long categoriaId){
        List<ItemDTO> itens = itemDTORepo.getItensForCardapio(nome, ativo, categoriaId);

        return itens;
    }


    //CRIAR, ALTERAR e EXCLUIR os itens

    @Transactional
    public void criarAlterarItem(Long       id,
                                 String     nome,
                                 String     descricao,
                                 BigDecimal valor,
                                 BigDecimal desconto,
                                 Integer    estoque,
                                 Long       categoriaId,
                                 Long       referencia_ext,
                                 String     ideusu){
        
        genSvc.validaUsuarioByIdeusu(ideusu);

        Boolean isNewItem = false;

        if(nome == null || nome.isBlank()) throw new ItemException("É preciso informar o nome do item!");
        //if(descricao == null || descricao.isBlank()) throw new ItemException("É preciso informar a descrição do item!");
        if(valor == null || valor.equals(BigDecimal.ZERO)) throw new ItemException("É preciso informar um valor para o item");
        if(valor.compareTo(BigDecimal.ZERO) < 0) throw new ItemException("O valor do item não pode ser menor que 0");
        //if(desconto == null || desconto.equals(BigDecimal.ZERO)) throw new ItemException("É preciso informar um desconto para o item!");
        if(valor.compareTo(desconto) < 0) throw new ItemException("O desconto do item não pode ser maior que o valor do item");

        genSvc.usuarioTemPermissao("Editar Produtos", ideusu);

        Item item = itemRepo.findItemById(id);

        if(item == null){
            item = new Item();
            item.setIdeusu(ideusu);
            item.setCriadoEm(LocalDate.now());
            item.setAtivo(true);

            isNewItem = true;
        }

        if(categoriaId != null && categoriaId != 0){
            Categoria categoria = categoriaRepo.findCategoriaById(categoriaId);
            if(categoria == null) throw new CategoriaNotFoundException("Categoria informada [código " + categoriaId + "] não encontrada!");

            if(!categoria.getAtivo()) throw new CategoriaException("Categoria do Item esta inativa!");

            item.setCategoria(categoria);
        }

        item.setNome(nome);
        item.setDescricao(descricao);
        item.setValor(valor);
        item.setDesconto(desconto);
        item.setEstoque(estoque);
        item.setReferencia_ext(referencia_ext);

        itemRepo.save(item);

        userSvc.salvarHistoricoUsuario("Item '" + nome + "' " + (isNewItem? "Cadastrado" : "Atualizado"), ideusu);
    }

    @Transactional
    public void ativarInativarItem(Long id, Boolean ativar, String ideusu){
        genSvc.validaUsuarioByIdeusu(ideusu);

        genSvc.usuarioTemPermissao("Editar Produtos", ideusu);

        Item item = itemRepo.findItemById(id);
        if(item == null) throw new ItemException("Não encontrado o Item");

        item.setAtivo(ativar);

        itemRepo.save(item);

        userSvc.salvarHistoricoUsuario("Item '" + item.getNome() + "' " + (ativar? "Ativado" : "Desativado"), ideusu);
    }

    @Transactional
    public void excluiItem(Long id, String ideusu){
        genSvc.validaUsuarioByIdeusu(ideusu);

        genSvc.usuarioTemPermissao("Excluir Produtos", ideusu);

        String descItem = "";

        Item item = itemRepo.findItemById(id);
        if(item == null) throw new ItemException("Não encontrado o Item");

        descItem = item.getDescricao();

        itemRepo.delete(item);

        userSvc.salvarHistoricoUsuario("Item '" + descItem + "' excluido", ideusu);
    }

    @Transactional
    public void adapterVincularItemImage(MultipartFile[] images, Long itemId, String ideusu) throws IOException{
       for (MultipartFile image : images) {
            vincularItemImage(image, itemId, ideusu);
        }
    }

    @Transactional
    private void vincularItemImage(MultipartFile image, Long itemId, String ideusu) throws IOException{
       genSvc.validaUsuarioByIdeusu(ideusu);

        genSvc.usuarioTemPermissao("Editar Produtos", ideusu);

       Item item = itemRepo.findItemById(itemId);
        if(item == null) throw new ItemException("Não encontrado o Item");

       Integer seqimg = itemImgRepo.findMaxSeqByItemId(itemId);
       if(seqimg == null) seqimg = 0;

       seqimg = seqimg + 1;

       String original = Objects.requireNonNull(image.getOriginalFilename());
       String ext = original.substring(original.lastIndexOf("."));

       String newName = "item_" + String.valueOf(item.getId()) + "_" + seqimg + ext;

       Path fileNameAndPath = Paths.get(itensDirectory, newName);
       Files.write(fileNameAndPath, image.getBytes());

       ItemMedia media = new ItemMedia();
       media.setId(new ItemMediaId(itemId, seqimg));
       media.setItem(item);
       media.setCriadoEm(LocalDate.now());
       media.setDescricao(newName);
       media.setIdeusu(ideusu);
    
       itemImgRepo.save(media);
    }

    @Transactional
    private void removerMediaItem(Long itemId, Integer sequencia, String ideusu) throws IOException{
        genSvc.validaUsuarioByIdeusu(ideusu);

        genSvc.usuarioTemPermissao("Editar Produtos", ideusu);

       Item item = itemRepo.findItemById(itemId);
        if(item == null) throw new ItemException("Não encontrado o Item");

       if(sequencia == null || sequencia <= 0) throw new ItemException("É preciso informar a sequencia da media para remover!");

       ItemMedia media = itemImgRepo.findMediaById(itemId, sequencia);
       if(media == null) throw new ItemException("Não envontrado registro da media no banco!");

       Path fileNameAndPath = Paths.get(itensDirectory, media.getDescricao());
       Files.delete(fileNameAndPath);

       itemImgRepo.delete(media);
    }

    @Transactional
    private void removerTodosMediaItem(String ideusu) throws IOException{
       List<ItemMedia> itens = itemImgRepo.findAll();
        if(itens == null) return;

       for (ItemMedia item : itens) {
           removerMediaItem(item.getId().getIdItem(), item.getId().getSeq(), ideusu);
       }
    }

    public List<ItemMedia> getListMediaItem(Long idItem){
        List<ItemMedia> itens = itemImgRepo.findAllByIdItem(idItem);

        return itens;
    }
}
