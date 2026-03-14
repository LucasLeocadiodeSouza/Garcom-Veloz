package com.back.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.back.demo.exception.EmpresaNotFoundException;
import com.back.demo.exception.ItemNotFoundException;
import com.back.demo.exception.PedidoException;
import com.back.demo.exception.PedidoNotFoundException;
import com.back.demo.model.Empresa;
import com.back.demo.model.Item;
import com.back.demo.model.Pedido;
import com.back.demo.model.PedidoItem;
import com.back.demo.model.PedidoItemDTO;
import com.back.demo.model.PedidoItemId;
import com.back.demo.model.PedidoDTO;
import com.back.demo.repository.EmpresaRepository;
import com.back.demo.repository.ItemRepository;
import com.back.demo.repository.PedidoItemRepository;
import com.back.demo.repository.PedidoRepository;
import jakarta.transaction.Transactional;

@Service
public class PedidoSvc {
    @Autowired
    private PedidoRepository pedidoRepo;

    @Autowired
    private PedidoItemRepository pedidoItemRepo;

    @Autowired
    private ItemRepository itemRepo;

    @Autowired
    private EmpresaRepository empresaRepo;




    public List<PedidoItem> getItensPedidos(Long pedidoId) {
        return pedidoItemRepo.findAllItensByPedido(pedidoId);
    }

    public List<Pedido> getListPedidos(Integer mesa, Integer estado){

        List<Pedido> pedidos = new ArrayList<>();

        if(estado != null && estado != 0) pedidos = pedidoRepo.findByEstado(estado);
        else pedidos = pedidoRepo.findAll();
        
        if(mesa != null && mesa != 0) {
            List<Pedido> pedidosMesa = pedidoRepo.findByMesa(mesa);

            for (Pedido pedidoMesa : pedidosMesa) {
                if(!pedidos.contains(pedidoMesa)) pedidos.remove(pedidoMesa);
            }
        }

        return pedidos;
    }



    public List<PedidoDTO> getListPedidosDTO(Integer mesa, Integer estado) {
        List<Pedido> pedidos = getListPedidos(mesa, estado);
        List<PedidoDTO> dtos = new ArrayList<>();

        for (Pedido p : pedidos) {
            BigDecimal total             = pedidoItemRepo.getValueOrder(p.getId());
            if(total == null) total      = BigDecimal.ZERO;

            List<PedidoItemDTO> itemDTOs = getListPedidosItemDTO(p.getId());

            System.out.println(total);

            if (p.getGorgeta() != null) total = total.add(p.getGorgeta());

            dtos.add(new PedidoDTO(
                     p.getId(),
                     p.getEstado(),
                     p.getObservacao(),
                     p.getGorgeta(),
                     p.getMesa(),
                     p.getCriadoEm(),
                     p.getHorario(),
                     p.getIdeusu(),
                     total,
                     itemDTOs));
        }

        return dtos;
    }

    public List<PedidoItemDTO> getListPedidosItemDTO(Long idPedido) {
        Pedido pedido = pedidoRepo.findPedidoById(idPedido);

        if(pedido == null) throw new PedidoNotFoundException("Não encontrado um pedido para o código informado");

        BigDecimal total = BigDecimal.ZERO;
        List<PedidoItemDTO> itemDTOs = new ArrayList<>();

        for (PedidoItem pi : pedido.getItens()) {
            BigDecimal valorItem = pi.getItem().getValor();
            if (pi.getItem().getDesconto() != null) {
                valorItem = valorItem.subtract(pi.getItem().getDesconto());
            }
            total = total.add(valorItem);

            itemDTOs.add(new PedidoItemDTO(
                         pedido.getId(),
                         pi.getItem().getId(),
                         pi.getItem().getNome(),
                         pi.getItem().getDescricao(),
                         pi.getQuantidade(),
                         valorItem));
        }

        return itemDTOs;
    }

    // CRIAR, ALTERAR e EXCLUIR os pedidos

    @Transactional
    public void criarAlterarPedido(Long       id,
                                   String     observacao,
                                   BigDecimal gorgeta,
                                   Integer    mesa,
                                   Long       idEmpresa,
                                   String     ideusu) {

        if (mesa == null || mesa == 0) throw new PedidoException("É preciso informar o número da mesa para criar o pedido!");

        Empresa empresa = empresaRepo.findEmpresaById(idEmpresa);
        if (empresa == null) throw new EmpresaNotFoundException("Empresa informada não encontrado!");

        Pedido pedido = pedidoRepo.findPedidoById(id);

        if (pedido == null) {
            pedido = new Pedido();
            pedido.setEstado(1);
            pedido.setIdeusu(ideusu);
            pedido.setCriadoEm(LocalDate.now());
            pedido.setHorario(LocalTime.now());
            pedido.setEmpresa(empresa);
        }

        pedido.setGorgeta(gorgeta);
        pedido.setMesa(mesa);
        pedido.setObservacao(observacao);

        pedidoRepo.save(pedido);
    }

    @Transactional
    public void excluiPedido(Long id, String ideusu) {
        Pedido pedido = pedidoRepo.findPedidoById(id);
        if (pedido == null) throw new PedidoNotFoundException("Não encontrado o Pedido");

        pedidoRepo.delete(pedido);
    }

    @Transactional
    public void vinculaItemPedido(Long    pedidoId,
                                  Long    itemId,
                                  Integer quantidade,
                                  String  ideusu) {

        if (pedidoId == null || pedidoId == Long.valueOf(0)) throw new PedidoException("É preciso informar o número do pedido para vincular ao item!");
        if (itemId == null || itemId == Long.valueOf(0)) throw new PedidoException("É preciso informar o código do item para vincular ao pedido!");

        Pedido pedido = pedidoRepo.findPedidoById(pedidoId);
        if (pedido == null) throw new PedidoNotFoundException("Não encontrado o Pedido");

        Item item = itemRepo.findItemById(itemId);
        if (item == null) throw new ItemNotFoundException("Não encontrado o Item");

        PedidoItem vinculoPedidoItem = pedidoItemRepo.findPedidoItemById(pedidoId, itemId);
        if (vinculoPedidoItem == null) {
            criarAlterarItemPedido(pedidoId, itemId, quantidade, ideusu);
            return;
        }

        Integer quantidadeOld = vinculoPedidoItem.getQuantidade();
        vinculoPedidoItem.setQuantidade(quantidadeOld + quantidade);

        pedidoItemRepo.save(vinculoPedidoItem);
    }

    @Transactional
    public void criarAlterarItemPedido(Long    pedidoId,
                                       Long    itemId,
                                       Integer quantidade,
                                       String  ideusu) {

        if (pedidoId == null || pedidoId == Long.valueOf(0)) throw new PedidoException("É preciso informar o número do pedido para vincular ao item!");
        if (itemId == null || itemId == Long.valueOf(0)) throw new PedidoException("É preciso informar o código do item para vincular ao pedido!");

        Pedido pedido = pedidoRepo.findPedidoById(pedidoId);
        if (pedido == null) throw new PedidoNotFoundException("Não encontrado o Pedido");

        Item item = itemRepo.findItemById(itemId);
        if (item == null) throw new ItemNotFoundException("Não encontrado o Item");

        PedidoItem vinculoPedidoItem = pedidoItemRepo.findPedidoItemById(pedidoId, itemId);
        if (vinculoPedidoItem == null){
            PedidoItemId id_vinculoPedidoItem = new PedidoItemId();
            id_vinculoPedidoItem.setIdItem(itemId);
            id_vinculoPedidoItem.setIdPedido(pedidoId);
    
            vinculoPedidoItem = new PedidoItem();
            vinculoPedidoItem.setId(id_vinculoPedidoItem);
            vinculoPedidoItem.setItem(item);
            vinculoPedidoItem.setPedido(pedido);
            vinculoPedidoItem.setIdeusu(ideusu);
            vinculoPedidoItem.setCriadoEm(LocalDate.now());
            
        }
        
        vinculoPedidoItem.setQuantidade(quantidade);

        pedidoItemRepo.save(vinculoPedidoItem);
    }

    @Transactional
    public void excluiItemPedido(Long   pedidoId,
                                 Long   itemId,
                                 String ideusu){

        if (pedidoId == null || pedidoId == Long.valueOf(0))
            throw new PedidoException("É preciso informar o número do pedido para remover o item!");
        if (itemId == null || itemId == Long.valueOf(0))
            throw new PedidoException("É preciso informar o código do item para remover o pedido!");

        Pedido pedido = pedidoRepo.findPedidoById(pedidoId);
        if (pedido == null)
            throw new PedidoNotFoundException("Não encontrado o Pedido");

        Item item = itemRepo.findItemById(itemId);
        if (item == null)
            throw new ItemNotFoundException("Não encontrado o Item");

        PedidoItem vinculoPedidoItem = pedidoItemRepo.findPedidoItemById(pedidoId, itemId);
        if (vinculoPedidoItem == null)
            return;

        pedidoItemRepo.delete(vinculoPedidoItem);
    }

}
