package com.back.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.back.demo.exception.ItemNotFoundException;
import com.back.demo.exception.PedidoException;
import com.back.demo.exception.PedidoNotFoundException;
import com.back.demo.model.Item;
import com.back.demo.model.Pedido;
import com.back.demo.model.PedidoItem;
import com.back.demo.model.PedidoItemId;
import com.back.demo.repository.ItemRepository;
import com.back.demo.repository.PedidoItemRepository;
import com.back.demo.repository.PedidoRepository;

@Service
public class PedidoSvc {
    @Autowired
    private PedidoRepository pedidoRepo;

    @Autowired
    private PedidoItemRepository pedidoItemRepo;

    @Autowired
    private ItemRepository itemRepo;


    public void criarAlterarPedido(Long       id,
                                   String     observacao,
                                   BigDecimal gorgeta,
                                   Integer    mesa,
                                   String     ideusu)
                                   {        

        if(mesa == null || mesa == 0) throw new PedidoException("É preciso informar o número da mesa para criar o pedido!");

        Pedido pedido = pedidoRepo.findPedidoById(id);

        if(pedido == null){
            pedido = new Pedido();
            pedido.setEstado(1);
            pedido.setIdeusu(ideusu);
            pedido.setCriadoEm(LocalDate.now());
            pedido.setHorario(LocalTime.now());
        }

        pedido.setGorgeta(gorgeta);
        pedido.setMesa(mesa);
        pedido.setObservacao(observacao);

        pedidoRepo.save(pedido);
    }

    public void vinculaItemPedido(Long   pedidoId,
                                  Long   itemId,
                                  String ideusu)
                                  {        

        if(pedidoId == null || pedidoId == Long.valueOf(0)) throw new PedidoException("É preciso informar o número do pedido para vincular ao item!");
        if(itemId == null || itemId == Long.valueOf(0)) throw new PedidoException("É preciso informar o código do item para vincular ao pedido!");

        Pedido pedido = pedidoRepo.findPedidoById(pedidoId);
        if(pedido == null) throw new PedidoNotFoundException("Não encontrado o Pedido");

        Item item = itemRepo.findItemById(itemId);
        if(item == null) throw new ItemNotFoundException("Não encontrado o Item");

        PedidoItem vinculoPedidoItem = pedidoItemRepo.findPedidoItemById(pedidoId, itemId);
        if(vinculoPedidoItem != null) return;

        PedidoItemId id_vinculoPedidoItem = new PedidoItemId();
        id_vinculoPedidoItem.setIdItem(itemId);
        id_vinculoPedidoItem.setIdPedido(pedidoId);

        vinculoPedidoItem = new PedidoItem();
        vinculoPedidoItem.setId(id_vinculoPedidoItem);
        vinculoPedidoItem.setItem(item);
        vinculoPedidoItem.setPedido(pedido);
        vinculoPedidoItem.setIdeusu(ideusu);
        vinculoPedidoItem.setCriadoEm(LocalDate.now());

        pedidoItemRepo.save(vinculoPedidoItem);
    }
}
