package com.back.demo.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.back.demo.model.UserDTO;
import jakarta.persistence.EntityManager;

@Repository
public class UserDTORepository {
    private final EntityManager em;

    public UserDTORepository(EntityManager em){
        this.em = em;
    }


    public Long getCountUsuarioByStatus(Boolean status, String nome, Long idEmpresa, Long idPerfil){
        String query = "SELECT COUNT(u) " + 
                       "FROM Usuario u JOIN u.perfil p " +
                       "WHERE u.ativo = " + status.toString();

        Boolean filtroPorDescricao = nome != null && !nome.isBlank();
        Boolean filtroPorPerfil    = idPerfil != null && idPerfil != 0;
        Boolean filtroPorEmpresa   = idEmpresa != null && idEmpresa != 0;

        if(filtroPorDescricao) query += " AND u.nome LIKE CONCAT('%', :nome ,'%') ";
        if(filtroPorPerfil)    query += " AND p.id = :idPerfil";
        if(filtroPorEmpresa)   query += " AND e.id = :idEmpresa";

        var q = em.createQuery(query, Long.class);

        if(filtroPorDescricao) q.setParameter("nome", nome);
        if(filtroPorPerfil)    q.setParameter("idPerfil", filtroPorPerfil);
        if(filtroPorEmpresa)   q.setParameter("idEmpresa", idEmpresa);

        return q.getSingleResult();
    }

    public List<UserDTO> getListUsuarios(String nome, Boolean ativo, Long idEmpresa, Long idPerfil){
        Boolean filtroPorDescricao = nome != null && !nome.isBlank();
        Boolean filtroPorPerfil    = idPerfil != null && idPerfil != 0;
        Boolean filtroPorEmpresa   = idEmpresa != null && idEmpresa != 0;
        Boolean filtroPorStatus    = ativo != null;
        Boolean temAnd             = false;

        String totalResultAtivos;
        String totalResultInativos;

        if(filtroPorStatus){
            totalResultAtivos   = (ativo?"COUNT(U)":"0");
            totalResultInativos = (ativo?"0":"COUNT(U)");
        }
        else {
            totalResultAtivos   = getCountUsuarioByStatus(true, nome, idEmpresa, idPerfil).toString();
            totalResultInativos = getCountUsuarioByStatus(false, nome, idEmpresa, idPerfil).toString();
        }

        String query = "SELECT new UserDTO(" + 
                       "u.id, " +
                       "u.nome, " +
                       "u.email, " +
                       "p.id, " +
                       "p.descricao, " +
                       "u.ativo, " +
                       "COUNT(u), " +
                       totalResultAtivos + ", " +
                       totalResultInativos + ") " +
                       "FROM Usuario u " + 
                       "JOIN u.perfil p " +
                       "JOIN u.empresa e";

        if(filtroPorDescricao){
            query += " WHERE u.nome LIKE CONCAT('%', :nomeItem ,'%') ";
            temAnd = true;
        }
        if(filtroPorPerfil){
            query += (temAnd?" AND ":" ") + "p.id = :idPerfil";
            temAnd = true;
        }
        if(filtroPorEmpresa){
            query += (temAnd?" AND ":" ") + "e.id = :idEmpresa";
            temAnd = true;
        }
        if(filtroPorStatus) query += (temAnd?" AND ":" ") + "u.ativo = :ativo";

        var q = em.createQuery(query, UserDTO.class);

        if(filtroPorDescricao) q.setParameter("nomeItem", nome);
        if(filtroPorPerfil)    q.setParameter("ativo", ativo);
        if(filtroPorEmpresa)   q.setParameter("idEmpresa", idEmpresa);
        if(filtroPorStatus)    q.setParameter("idPerfil", idPerfil);

        return q.getResultList();
    }


    public List<UserDTO> getAllRestricoesPerfil(){
        String query = "SELECT new UserDTO(" + 
                       "p.id, " +
                       "p.descricao, " +
                       "r.id, " +
                       "r.descricao) " +
                       "FROM RestricaoPerfil rp " + 
                       "JOIN rp.perfil p " +
                       "JOIN r.restricao r ";

        var q = em.createQuery(query, UserDTO.class);

        return q.getResultList();
    }
}
