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


    public List<UserDTO> getListUsuarios(String nome, String ativo, Long idEmpresa, Long idPerfil){
        Boolean filtroPorDescricao = nome != null && !nome.isBlank();
        Boolean filtroPorPerfil    = idPerfil != null && idPerfil != 0;
        Boolean filtroPorEmpresa   = idEmpresa != null && idEmpresa != 0;
        Boolean filtroPorStatus    = ativo != null && !ativo.isBlank();
        Boolean temAnd             = false;

        String query = "SELECT new UserDTO(" + 
                       "u.id, " +
                       "u.nome, " +
                       "u.email, " +
                       "u.telefone, " +
                       "p.id, " +
                       "p.descricao, " +
                       "u.ativo, " +
                       "e.id, " +
                       "u.criadoEm) " +
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
        if(filtroPorStatus)    q.setParameter("ativo", ativo == "A");
        if(filtroPorEmpresa)   q.setParameter("idEmpresa", idEmpresa);
        if(filtroPorPerfil)    q.setParameter("idPerfil", idPerfil);

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
                       "JOIN rp.restricao r ";

        var q = em.createQuery(query, UserDTO.class);

        return q.getResultList();
    }
}
