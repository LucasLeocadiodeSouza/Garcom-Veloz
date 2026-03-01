package com.back.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.back.demo.model.Perfil;

public interface PerfilRepository extends JpaRepository<Perfil, Long>{
    @Query("SELECT p FROM Perfil p WHERE p.id = :id")
    Perfil findPerfilById(@Param("id") Long id);

    @Query("SELECT p FROM Perfil p WHERE p.descricao LIKE :descricao")
    Perfil findPerfilByDescricao(@Param("descricao") String descricao);

    @Query("SELECT p FROM Perfil p WHERE p.ativo = :status")
    List<Perfil> findPerfilByStatus(@Param("status") Boolean status);
}
