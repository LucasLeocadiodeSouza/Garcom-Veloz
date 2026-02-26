package com.back.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.back.demo.exception.ItemException;
import com.back.demo.exception.PedidoException;
import com.back.demo.exception.PedidoNotFoundException;
import com.back.demo.model.Item;
import com.back.demo.model.Pedido;
import com.back.demo.repository.ItemRepository;

@Service
public class ItemSvc {

    @Autowired
    private ItemRepository itemRepo;


    //CRIAR, ALTERAR e EXCLUIR os itens

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
        }

        item.setNome(nome);
        item.setDescricao(descricao);
        item.setDesconto(desconto);
        item.setValor(valor);

        itemRepo.save(item);
    }

    public void excluiItem(Long id){
        Item item = itemRepo.findItemById(id);
        if(item == null) throw new ItemException("Não encontrado o Item");

        itemRepo.delete(item);
    }
}
