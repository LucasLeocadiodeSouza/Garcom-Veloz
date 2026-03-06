package com.back.demo.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.back.demo.model.CategoriaDTO;
import jakarta.persistence.EntityManager;

@Repository
public class CategoriaDTORepository {
    private final EntityManager em;

    public CategoriaDTORepository(EntityManager em){
        this.em = em;
    }


    public Long countCategoriaByStatus(Boolean status, String search){
        String query = "SELECT COUNT(c) FROM Categoria c WHERE c.ativo = :status";

        Boolean filtroPorDescricao = search != null && !search.isBlank();

        if(filtroPorDescricao) query += " AND  (c.descricao LIKE CONCAT('%', :search ,'%')) ";

        var q = em.createQuery(query, Long.class);

        if(filtroPorDescricao) q.setParameter("search", search);

        return q.getSingleResult();
    }

    public Long getCountProductByCategoria(Long categoriaId, String status, String search){
        String query = "SELECT COUNT(i) " + 
                       "FROM  Item i " +
                       "JOIN Categoria c ON i.id = c.id ";

        Boolean filtroPorDescricao = search != null && !search.isBlank();
        Boolean filtroPorStatus    = status != null && !status.isBlank();
        Boolean filtroPorCodigo    = categoriaId != null && categoriaId != 0;
        Boolean temAnd             = false;

        if(filtroPorCodigo) query += " WHERE c.id = :categoriaId ";
        else{
            if(filtroPorStatus) {
                query += (temAnd? " AND " : " WHERE ") + " c.ativo = :status ";
                temAnd = true;
            }
            if(filtroPorDescricao) {
                query += (temAnd? " AND " : " WHERE ") + " (c.descricao LIKE CONCAT('%', :search ,'%')) ";
                temAnd = true;
            }
        }

        var q = em.createQuery(query, Long.class);

        if(filtroPorCodigo) q.setParameter("categoriaId", categoriaId);
        else {
            if(filtroPorStatus) q.setParameter("status", status == "A");
            if(filtroPorDescricao) q.setParameter("search", search);
        }

        return q.getSingleResult();
    }

    public CategoriaDTO getStatsCategoria(String status, String search){
        Long countAtivos   = status == "A"  || status.isBlank()?  countCategoriaByStatus(true, search)  : 0L;
        Long countInativos = status == "I" || status.isBlank()? countCategoriaByStatus(false, search) : 0L;
        Long countByProd   = getCountProductByCategoria(null, status, search);

        return new CategoriaDTO(countByProd, countAtivos, countInativos);
    }

    public List<CategoriaDTO> getCategoriaList(String status, String search){
        Boolean filtroPorDescricao = search != null && !search.isBlank();
        Boolean filtroPorStatus    = status != null && !status.isBlank();
        Boolean temAnd             = false;

        String query = "SELECT new CategoriaDTO(" + 
                       "c.id, " +
                       "c.descricao, " +
                       "c.icone, " +
                       "c.cor, " +
                       "c.ativo, " +
                       "(SELECT COUNT(i) FROM Item i WHERE i.categoria.id = c.id), " +
                       "c.criadoEm" +
                       ") " +
                       "FROM Categoria c ";

        if(filtroPorStatus){
            query += "WHERE c.ativo = :status";
            temAnd = true;
        }
        if(filtroPorDescricao) query += (temAnd? " AND " : " WHERE ") + " (c.descricao LIKE CONCAT('%', :search ,'%')) ";

        var q = em.createQuery(query, CategoriaDTO.class);

        if(filtroPorDescricao) q.setParameter("search", search);
        if(filtroPorStatus) q.setParameter("status", status == "A");

        return q.getResultList();
    }
}
