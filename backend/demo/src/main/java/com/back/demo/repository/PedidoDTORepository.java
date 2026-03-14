package com.back.demo.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.back.demo.model.PedidoItemDTO;

import jakarta.persistence.EntityManager;

@Repository
public class PedidoDTORepository {
    private final EntityManager em;

    public PedidoDTORepository(EntityManager em){
        this.em = em;
    }
    
    public List<PedidoItemDTO> getListItensPedidos(Long idPedido, Long idItem, Integer mesa, Integer estado, Integer limit){
        String query = "SELECT new PedidoDTO(" + 
                       "p.id,"  +
                       "i.id,"    +
                       "i.nome,"  +
                       "(i.valor - i.desconto)" +
                       ") " +
                       "FROM PedidoItem pi " +
                       "JOIN pi.item i " +
                       "JOIN pi.pedido p ";

        Boolean filtroPorEstado = estado   != null && estado   != 0;
        Boolean filtroPorMesa   = mesa     != null && mesa     != 0;
        Boolean filtroPorPedido = idPedido != null && idPedido != 0;
        Boolean filtroPorItem   = idItem   != null && idItem   != 0;

        Boolean temAnd = false;

        if(filtroPorMesa){
            query += " WHERE p.mesa = :mesa ";
            temAnd = true;
        }
        if(filtroPorEstado){
            query += (temAnd?" AND ":" WHERE ") + "p.estado = :estado";
            temAnd = true;
        }
        if(filtroPorPedido){
            query += (temAnd?" AND ":" WHERE ") + "p.id = :idPedido";
            temAnd = true;
        }
        if(filtroPorItem){
            query += (temAnd?" AND ":" WHERE ") + "i.id = :idItem";
            temAnd = true;
        }

        var q = em.createQuery(query, PedidoItemDTO.class);

        if(filtroPorMesa)   q.setParameter("mesa",     mesa);
        if(filtroPorEstado) q.setParameter("estado",   estado);
        if(filtroPorPedido) q.setParameter("idPedido", idPedido);
        if(filtroPorItem)   q.setParameter("idItem",   idItem);

        q.setMaxResults(limit);

        return q.getResultList();
    }
}
