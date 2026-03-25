package com.back.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.back.demo.model.UsuarioHistorico;
import com.back.demo.model.UsuarioHistoricoId;

@Repository
public interface UsuarioHistoricoRepository extends JpaRepository<UsuarioHistorico, UsuarioHistoricoId>{
    @Query("SELECT hist FROM UsuarioHistorico hist WHERE hist.id.idUsuario = :idUsuario")
    List<UsuarioHistorico> findHistoricoByUsuario(@Param("idUsuario") Long idUsuario);

    List<UsuarioHistorico> findTop5ByIdIdUsuarioOrderByCriadoEmDesc(Long idUsuario);

    @Query("SELECT MAX(hi.id.seq) FROM UsuarioHistorico hi WHERE hi.id.idUsuario = :idUsuario")
    Long findMaxSeqByHistoricoUsuario(@Param("idUsuario") Long idUsuario);
}
