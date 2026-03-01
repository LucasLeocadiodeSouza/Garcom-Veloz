package com.back.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.back.demo.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>{
    @Query("SELECT c FROM Categoria c WHERE c.id = :id")
    Categoria findCategoriaById(@Param("id") Long id);
    
    @Query("SELECT c FROM Categoria c WHERE c.descricao LIKE CONCAT('%', :nome, '%')")
    List<Categoria> findCategoriaByDescricao(@Param("nome") String nome);

    @Query("SELECT c FROM Categoria c WHERE c.ativo = :ativo")
    List<Categoria> findAllCategoriaByStatus(@Param("ativo") Boolean ativo);
}
