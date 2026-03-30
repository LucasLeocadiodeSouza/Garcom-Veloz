package com.back.demo.repository;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.back.demo.model.ItemDTO;
import jakarta.persistence.EntityManager;

@Repository
public class ItemDTORepository {
    private final EntityManager em;

    public ItemDTORepository(EntityManager em){
        this.em = em;
    }

    // public Long getCountItensByStatus(Boolean status, String nome, Long idCategoria){
    //     String query = "SELECT COUNT(i) FROM Item i " + 
    //                    "JOIN Categoria cat ON cat.id = i.idCategoria " + //JOIN i.categoria cat
    //                    "WHERE i.ativo = " + status.toString();

    //     Boolean filtroPorDescricao = nome != null && !nome.isBlank();
    //     Boolean filtroPorCategoria = idCategoria != null && idCategoria != 0;

    //     if(filtroPorDescricao) query += " AND i.nome LIKE CONCAT('%', :nomeItem ,'%') ";
    //     if(filtroPorCategoria) query += " AND cat.id = :idCategoria";

    //     var q = em.createQuery(query, Long.class);

    //     if(filtroPorDescricao) q.setParameter("nomeItem", nome);
    //     if(filtroPorCategoria) q.setParameter("idCategoria", idCategoria);

    //     return q.getSingleResult();
    // }

    public List<ItemDTO> getListItem(String nome, String ativo, Long idCategoria){
        String query = "SELECT new ItemDTO(" + 
                       "i.id, " +
                       "cat.id, " +
                       "cat.descricao, " +
                       "i.nome, " +
                       "i.descricao, " +
                       "i.valor, " +
                       "i.desconto, " +
                       "i.estoque, " +
                       "i.ativo, " +
                       "i.criadoEm, " +
                       "i.ideusu, " +
                       "i.referencia_ext " +
                       ") " +
                       " FROM Item i JOIN i.categoria cat ";

        Boolean filtroPorDescricao = nome != null && !nome.isBlank();
        Boolean filtroPorCategoria = idCategoria != null && idCategoria != 0;
        Boolean filtroPorStatus    = ativo != null && !ativo.isBlank();

        Boolean temAnd = false;

        if(filtroPorDescricao){
            query += " WHERE i.nome LIKE CONCAT('%', :nomeItem ,'%') ";
            temAnd = true;
        }
        if(filtroPorCategoria){
            query += (temAnd?" AND ":" WHERE ") + "cat.id = :idCategoria";
            temAnd = true;
        }
        if(filtroPorStatus) query += (temAnd?" AND ":" WHERE ") + "i.ativo = :isActive";

        var q = em.createQuery(query, ItemDTO.class);

        if(filtroPorDescricao) q.setParameter("nomeItem", nome);
        if(filtroPorStatus)    q.setParameter("isActive", ativo.equals("A"));
        if(filtroPorCategoria) q.setParameter("idCategoria", idCategoria);

        return q.getResultList();
    }

    public List<ItemDTO> getItensForCardapio(String nome, String ativo, Long idCategoria){
        String query = "SELECT new ItemDTO(" + 
                       "i.id, " +
                       "i.nome, " +
                       "i.descricao, " +
                       "cat.id, " +
                       "cat.descricao, " +
                       "i.estoque, " +
                       "i.valor, " +
                       "i.desconto, " +
                       "(SELECT image.descricao FROM ItemMedia image WHERE image.id.idItem = i.id ORDER BY image.id.seq ASC LIMIT 1) " +
                       ") " +
                       " FROM Item i JOIN i.categoria cat ";

        Boolean filtroPorDescricao = nome != null && !nome.isBlank();
        Boolean filtroPorCategoria = idCategoria != null && idCategoria != 0;
        Boolean filtroPorStatus    = ativo != null && !ativo.isBlank();

        Boolean temAnd = false;

        if(filtroPorDescricao){
            query += " WHERE i.nome LIKE CONCAT('%', :nomeItem ,'%') ";
            temAnd = true;
        }
        if(filtroPorCategoria){
            query += (temAnd?" AND ":" WHERE ") + "cat.id = :idCategoria";
            temAnd = true;
        }
        if(filtroPorStatus) query += (temAnd?" AND ":" WHERE ") + "i.ativo = :isActive";

        var q = em.createQuery(query, ItemDTO.class);

        if(filtroPorDescricao) q.setParameter("nomeItem", nome);
        if(filtroPorStatus)    q.setParameter("isActive", ativo.equals("A"));
        if(filtroPorCategoria) q.setParameter("idCategoria", idCategoria);

        return q.getResultList();
    }

}
