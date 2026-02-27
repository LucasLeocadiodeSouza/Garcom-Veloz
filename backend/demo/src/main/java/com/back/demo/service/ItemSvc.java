package com.back.demo.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.back.demo.exception.ItemException;
import com.back.demo.model.Item;
import com.back.demo.model.ItemMedia;
import com.back.demo.model.ItemMediaId;
import com.back.demo.repository.ItemMediaRepository;
import com.back.demo.repository.ItemRepository;

import jakarta.transaction.Transactional;

@Service
public class ItemSvc {

    @Autowired
    private ItemRepository itemRepo;

    @Autowired
    private ItemMediaRepository itemImgRepo;

    private String itensDirectory = System.getProperty("user.dir") + "/media/itens";


    public List<Item> getListItem(String nome, Boolean ativo){
        List<Item> itens = new ArrayList<>();

        if(nome != null && !nome.isBlank()) itens = itemRepo.findItemByNome(nome);
        if(ativo){
            List<Item> itensAtivos = itemRepo.findItemByStatus(ativo);

            if(itens != null){
                for (Item itemAtivo : itensAtivos) {
                    if(!itens.contains(itemAtivo)) itens.remove(itemAtivo);
                }
            }
        } else itens = itemRepo.findAll();

        return itens;
    }


    //CRIAR, ALTERAR e EXCLUIR os itens

    @Transactional
    public void criarAlterarItem(Long       id,
                                 String     nome,
                                 String     descricao,
                                 BigDecimal valor,
                                 BigDecimal desconto,
                                 String     ideusu)
                                 {        

        if(nome == null || nome.isBlank()) throw new ItemException("É preciso informar o nome do item!");
        //if(descricao == null || descricao.isBlank()) throw new ItemException("É preciso informar a descrição do item!");
        if(valor == null || valor.equals(BigDecimal.ZERO)) throw new ItemException("É preciso informar um valor para o item");
        //if(desconto == null || desconto.equals(BigDecimal.ZERO)) throw new ItemException("É preciso informar um desconto para o item!");

        Item item = itemRepo.findItemById(id);

        if(item == null){
            item = new Item();
            item.setIdeusu(ideusu);
            item.setCriadoEm(LocalDate.now());
            item.setAtivo(true);
        }

        item.setNome(nome);
        item.setDescricao(descricao);
        item.setDesconto(desconto);
        item.setValor(valor);

        itemRepo.save(item);
    }

    @Transactional
    public void ativarInativarItem(Long id, Boolean ativar){
        Item item = itemRepo.findItemById(id);
        if(item == null) throw new ItemException("Não encontrado o Item");

        item.setAtivo(ativar);

        itemRepo.save(item);
    }

    @Transactional
    public void excluiItem(Long id){
        Item item = itemRepo.findItemById(id);
        if(item == null) throw new ItemException("Não encontrado o Item");

        itemRepo.delete(item);
    }

    @Transactional
    private void vincularItemImage(MultipartFile image, Long itemId) throws IOException{
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
    
       itemImgRepo.save(media);
    }

    @Transactional
    private void removerItemImage(Long itemId, Integer sequencia) throws IOException{
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
    private void removerTodosItemImage() throws IOException{
       List<ItemMedia> itens = itemImgRepo.findAll();
        if(itens == null) return;

       for (ItemMedia item : itens) {
           removerItemImage(item.getId().getIdItem(), item.getId().getSeq());
       }

    }
}
