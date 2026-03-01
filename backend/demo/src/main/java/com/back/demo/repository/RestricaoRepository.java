package com.back.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.back.demo.model.Restricao;

@Repository
public interface RestricaoRepository extends JpaRepository<Restricao, Long>{
    @Query("SELECT r FROM Restricao r WHERE r.id = :id")
    Restricao findRestricaoById(@Param("id") Long id);

    @Query("SELECT r FROM Restricao r WHERE r.descricao LIKE CONCAT('%', :descricao, '%')")
    List<Restricao> findRestricaoByDescricao(@Param("descricao") String descricao);

    @Query("SELECT r FROM Restricao r WHERE r.ativo = :status")
    List<Restricao> findRestricaoByStatus(@Param("status") Boolean status);
}
