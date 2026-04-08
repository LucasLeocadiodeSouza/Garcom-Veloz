package com.back.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.back.demo.model.RestricaoTela;
import com.back.demo.model.RestricaoTelaId;

public interface RestricaoTelaRepository extends JpaRepository<RestricaoTela, RestricaoTelaId> {

    @Query("SELECT rt FROM RestricaoTela rt WHERE rt.id.idPerfil = :idPerfil")
    List<RestricaoTela> findByIdPerfil(@Param("idPerfil") Long idPerfil);

    @Query("SELECT rt FROM RestricaoTela rt WHERE rt.id.idPerfil = :idPerfil AND rt.ativo = 1")
    List<RestricaoTela> findAtivasByIdPerfil(@Param("idPerfil") Long idPerfil);

    @Query("SELECT rt.id.idPerfil, rt.perfil.descricao, rt.id.idTela, rt.tela.label " +
           "FROM RestricaoTela rt WHERE rt.ativo = 1 ORDER BY rt.tela.label")
    List<Object[]> findAllFlat();

    @Query("SELECT rt FROM RestricaoTela rt WHERE rt.id.idPerfil = :idPerfil AND rt.id.idTela = :idTela")
    RestricaoTela findByIdPerfilAndIdTela(@Param("idPerfil") Long idPerfil, @Param("idTela") Long idTela);
}
