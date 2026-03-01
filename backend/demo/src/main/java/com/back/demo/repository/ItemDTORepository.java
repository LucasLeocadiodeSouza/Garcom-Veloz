package com.back.demo.repository;

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

    public Long getCountItensByStatus(Boolean status, String nome, Long idCategoria){
        String query = "SELECT COUNT(i) FROM Item i " + 
                       "JOIN Categoria cat ON cat.id = i.idCategoria " + //JOIN i.categoria cat
                       "WHERE i.ativo = " + status.toString();

        Boolean filtroPorDescricao = nome != null && !nome.isBlank();
        Boolean filtroPorCategoria = idCategoria != null && idCategoria != 0;

        if(filtroPorDescricao) query += " AND i.nome LIKE CONCAT('%', :nomeItem ,'%') ";
        if(filtroPorCategoria) query += " AND cat.id = :idCategoria";

        var q = em.createQuery(query, Long.class);

        if(filtroPorDescricao) q.setParameter("nomeItem", nome);
        if(filtroPorCategoria) q.setParameter("idCategoria", idCategoria);

        return q.getSingleResult();
    }

    public List<ItemDTO> getListItem(String nome, Boolean ativo, Long idCategoria){
        String query = "SELECT new ItemDTO(" + 
                       "i.id, " +
                       "cat.id, " +
                       "cat.descricao, " +
                       "i.nome, " +
                       "i.decricao, " +
                       "i.valor," +
                       "i.desconto, " +
                       "i.estoque, " +
                       "i.ativo, " +
                       "COUNT(i), " + // Totais
                       getCountItensByStatus(true, nome, idCategoria).toString() + ", " + // Totais
                       getCountItensByStatus(false, nome, idCategoria).toString() + ") " + // Totais 
                       " FROM Item i JOIN Categoria cat ON cat.id = i.idCategoria";

        Boolean filtroPorDescricao = nome != null && !nome.isBlank();
        Boolean filtroPorCategoria = idCategoria != null && idCategoria != 0;
        Boolean filtroPorStatus    = ativo != null;

        Boolean temAnd = false;

        if(filtroPorDescricao){
            query += " WHERE i.nome LIKE CONCAT('%', :nomeItem ,'%') ";
            temAnd = true;
        }
        if(filtroPorCategoria){
            query += (temAnd?" AND ":" ") + "cat.id = :idCategoria";
            temAnd = true;
        }
        if(filtroPorStatus) query += (temAnd?" AND ":" ") + "id.ativo = :ativo";

        var q = em.createQuery(query, ItemDTO.class);

        if(filtroPorDescricao) q.setParameter("nomeItem", nome);
        if(filtroPorCategoria) q.setParameter("ativo", ativo);
        if(filtroPorStatus)    q.setParameter("idCategoria", idCategoria);

        return q.getResultList();
    }
}
