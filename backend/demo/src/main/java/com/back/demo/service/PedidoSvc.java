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
import com.back.demo.exception.PedidoItemException;
import com.back.demo.exception.PedidoItemNotFoundException;
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

    private String getDescEstadoPedido(Integer estado){
        switch (estado) {
            case 0: return "Cancelada";
            case 1: return "Aberto";
            case 2: return "Encerrado";
        }

        return "";
    }

    private Integer getCodEstadoPedidoAberto(){ return 1; }
    private Integer getCodEstadoPedidoEncerrado(){ return 2; }

    public String getDescEstadoItem(Integer estado){
        switch (estado) {
            case 1: return "Aberto";
            case 2: return "Aguardando";
            case 3: return "Entregue";
        }

        return "";
    }

    private Integer getCodEstadoItemAberto(){ return 1; }
    private Integer getCodEstadoItemAguardando(){ return 2; }
    private Integer getCodEstadoItemEntregue(){ return 3; }

    public List<PedidoItem> getItensPedidos(Long pedidoId) {
        return pedidoItemRepo.findAllItensByPedido(pedidoId);
    }

    public List<Pedido> getListPedidos(Integer mesa, Integer estado, LocalDate dataInicio, LocalDate dataFim){
        List<Pedido> pedidos;
        boolean temPeriodo = dataInicio != null && dataFim != null;

        if (estado != null && estado != 0)  pedidos = temPeriodo ? pedidoRepo.findByEstadoAndPeriodo(estado, dataInicio, dataFim) : pedidoRepo.findByEstado(estado);
        else  pedidos = temPeriodo ? pedidoRepo.findByPeriodo(dataInicio, dataFim) : pedidoRepo.findAll();

        if (mesa != null && mesa != 0) {
            List<Pedido> pedidosMesa = pedidoRepo.findByMesa(mesa);
            pedidos.retainAll(pedidosMesa);
        }

        return pedidos;
    }

    public List<PedidoDTO> getListPedidosDTO(Integer mesa, Integer estado, LocalDate dataInicio, LocalDate dataFim) {
        List<Pedido> pedidos = getListPedidos(mesa, estado, dataInicio, dataFim);
        List<PedidoDTO> dtos = new ArrayList<>();

        for (Pedido p : pedidos) {
            BigDecimal total = pedidoItemRepo.getValueOrder(p.getId());
            if (total == null) total = BigDecimal.ZERO;

            List<PedidoItemDTO> itemDTOs = getListPedidosItemDTO(p.getId());

            if (p.getGorgeta() != null) total = total.add(p.getGorgeta());

            dtos.add(new PedidoDTO(
                     p.getId(),
                     p.getEstado(),
                     getDescEstadoPedido(p.getEstado()),
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

        if (pedido == null) throw new PedidoNotFoundException("Não encontrado um pedido para o código informado");

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
                         pi.getId().getSeq(),
                         pi.getItem().getNome(),
                         pi.getItem().getDescricao(),
                         pi.getQuantidade(),
                         pi.getEstado(),
                         getDescEstadoItem(pi.getEstado()),
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
    public void alterarEstadoPedido(Long    pedidoId,
                                    Integer estado,
                                    String  ideusu) {

        if (pedidoId == null || pedidoId == Long.valueOf(0)) throw new PedidoException("É preciso informar o número do pedido alterar o estado do item!");

        Pedido pedido = pedidoRepo.findPedidoById(pedidoId);
        if (pedido == null) throw new PedidoNotFoundException("Não encontrado o Pedido");

        if(estado == getCodEstadoPedidoEncerrado()){
          List<PedidoItem> itens = pedidoItemRepo.findAllItensByPedido(pedidoId);
          for (PedidoItem pedidoItem : itens) {
            if(pedidoItem.getEstado() == getCodEstadoItemAguardando()) throw new PedidoItemException("Não é possível encerrar um Pedido sem que todos seus itens estejam finalizados!");
          }
        }

        pedido.setEstado(estado);

        pedidoRepo.save(pedido);
    }

    // CRIAR, ALTERAR e EXCLUIR os itens do pedido


    //Usado para vincular e atualizar a quantidade de um item no pedidos
    @Transactional
    public void vinculaItemPedido(Long    pedidoId,
                                  Long    itemId,
                                  Long    seq,
                                  Integer quantidade,
                                  String  ideusu){
        Boolean temItemAberto = false;

        if (pedidoId == null || pedidoId == Long.valueOf(0)) throw new PedidoException("É preciso informar o número do pedido para vincular ao item!");
        if (itemId == null || itemId == Long.valueOf(0)) throw new PedidoException("É preciso informar o código do item para vincular ao pedido!");

        Pedido pedido = pedidoRepo.findPedidoById(pedidoId);
        if (pedido == null) throw new PedidoNotFoundException("Não encontrado o Pedido");
        if(pedido.getEstado() != getCodEstadoPedidoAberto()) throw new PedidoException("Somente é possivel Criar/Alterar pedidos que estão com status de ABERTO");

        Item item = itemRepo.findItemById(itemId);
        if (item == null) throw new ItemNotFoundException("Não encontrado o Item");

        List<PedidoItem> vinculosPedidoItem = pedidoItemRepo.findPedidoItemByItemAndPedido(pedidoId, itemId);
        PedidoItem vinculoPedidoItem = new PedidoItem();

        for (PedidoItem pedidoItem : vinculosPedidoItem) {
            if (pedidoItem.getEstado() == getCodEstadoItemAberto()) {
                temItemAberto = true;
                vinculoPedidoItem = pedidoItem;
            }
        }

        if (!temItemAberto) {
            criarAlterarItemPedido(pedidoId, itemId, seq, quantidade, ideusu);
            return;
        }

        Integer quantidadeOld = vinculoPedidoItem.getQuantidade();
        vinculoPedidoItem.setQuantidade(quantidadeOld + quantidade);

        pedidoItemRepo.save(vinculoPedidoItem);
    }


    // Usado nas alteracoes dos pedidos e para criar um novo vinculo
    @Transactional
    public void criarAlterarItemPedido(Long    pedidoId,
                                       Long    itemId,
                                       Long    seq,
                                       Integer quantidade,
                                       String  ideusu){

        if (pedidoId == null || pedidoId == Long.valueOf(0)) throw new PedidoException("É preciso informar o número do pedido para vincular ao item!");
        if (itemId == null || itemId == Long.valueOf(0)) throw new PedidoException("É preciso informar o código do item para vincular ao pedido!");

        Pedido pedido = pedidoRepo.findPedidoById(pedidoId);
        if (pedido == null) throw new PedidoNotFoundException("Não encontrado o Pedido");
        if(pedido.getEstado() != getCodEstadoPedidoAberto()) throw new PedidoException("Somente é possivel Criar/Alterar pedidos que estão com status de ABERTO");

        Item item = itemRepo.findItemById(itemId);
        if (item == null) throw new ItemNotFoundException("Não encontrado o Item");

        PedidoItem vinculoPedidoItem = pedidoItemRepo.findPedidoItemById(pedidoId, itemId, seq);
        if (vinculoPedidoItem != null) {
          if(vinculoPedidoItem.getEstado() == 3) throw new PedidoItemException("Não é possível alterar um Item do Pedido que já tenha sido entregue!");

        }else{
          Long newSeq = pedidoItemRepo.findMaxSeqByItemAndPedido(pedidoId, itemId);
          if (newSeq == null) newSeq = 0L;

          PedidoItemId id_vinculoPedidoItem = new PedidoItemId();
          id_vinculoPedidoItem.setIdItem(itemId);
          id_vinculoPedidoItem.setIdPedido(pedidoId);
          id_vinculoPedidoItem.setSeq(newSeq + 1L);

          vinculoPedidoItem = new PedidoItem();
          vinculoPedidoItem.setId(id_vinculoPedidoItem);
          vinculoPedidoItem.setItem(item);
          vinculoPedidoItem.setPedido(pedido);
          vinculoPedidoItem.setEstado(getCodEstadoItemAberto());
          vinculoPedidoItem.setIdeusu(ideusu);
          vinculoPedidoItem.setCriadoEm(LocalDate.now());
        }

        vinculoPedidoItem.setQuantidade(quantidade);

        pedidoItemRepo.save(vinculoPedidoItem);
    }

    @Transactional
    public void excluiItemPedido(Long   pedidoId,
                                 Long   itemId,
                                 Long   seq,
                                 String ideusu){

        if (pedidoId == null || pedidoId == Long.valueOf(0)) throw new PedidoException("É preciso informar o número do pedido para remover o item!");
        if (itemId == null || itemId == Long.valueOf(0)) throw new PedidoException("É preciso informar o código do item para remover o pedido!");

        Pedido pedido = pedidoRepo.findPedidoById(pedidoId);
        if (pedido == null) throw new PedidoNotFoundException("Não encontrado o Pedido");
        if(pedido.getEstado() != getCodEstadoPedidoAberto()) throw new PedidoException("Somente é possivel excluir pedidos que estão com status de ABERTO");

        Item item = itemRepo.findItemById(itemId);
        if (item == null) throw new ItemNotFoundException("Não encontrado o Item");

        PedidoItem vinculoPedidoItem = pedidoItemRepo.findPedidoItemById(pedidoId, itemId, seq);
        if (vinculoPedidoItem == null) return;

        if(vinculoPedidoItem.getEstado() != getCodEstadoItemAberto() && vinculoPedidoItem.getEstado() != getCodEstadoItemAguardando()) throw new PedidoItemException("Não é possível deletar um Pedido que não tenha o estado em Aberto ou Aguardando!");

        pedidoItemRepo.delete(vinculoPedidoItem);
    }

    @Transactional
    public void alterarEstadoItemPedido(Long    pedidoId,
                                        Long    itemId,
                                        Long    seq,
                                        Integer estado,
                                        String  ideusu) {

        if (pedidoId == null || pedidoId == Long.valueOf(0)) throw new PedidoException("É preciso informar o número do pedido alterar o estado do item!");
        if (itemId == null || itemId == Long.valueOf(0)) throw new PedidoException("É preciso informar o código do item para alterar o estado do pedido!");

        Pedido pedido = pedidoRepo.findPedidoById(pedidoId);
        if (pedido == null) throw new PedidoNotFoundException("Não encontrado o Pedido");

        if(pedido.getEstado() == 2) throw new PedidoException("Não é possivel alterar a situação de um item para um pedido que já foi finalizado");
        if(pedido.getEstado() == 0) throw new PedidoException("Não é possivel alterar a situação de um item de um pedido que está cancelado");

        Item item = itemRepo.findItemById(itemId);
        if (item == null) throw new ItemNotFoundException("Não encontrado o Item");

        PedidoItem vinculoPedidoItem = pedidoItemRepo.findPedidoItemById(pedidoId, itemId, seq);
        if (vinculoPedidoItem == null) throw new PedidoItemNotFoundException("Não encontrado o item vinculado ao pedido");

        //if(vinculoPedidoItem.getEstado() == 3 && estado == getCodEstadoItemAberto()) throw new PedidoItemException("Não é possível voltar um item entregue do pedido para aberto!");

        vinculoPedidoItem.setEstado(estado);

        pedidoItemRepo.save(vinculoPedidoItem);
    }

}
