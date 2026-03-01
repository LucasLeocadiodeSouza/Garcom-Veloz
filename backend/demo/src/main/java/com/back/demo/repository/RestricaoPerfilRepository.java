package com.back.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.back.demo.model.Perfil;
import com.back.demo.model.Restricao;
import com.back.demo.model.RestricaoPerfil;
import com.back.demo.model.RestricaoPerfilId;

public interface RestricaoPerfilRepository extends JpaRepository<RestricaoPerfil, RestricaoPerfilId>{
    @Query("SELECT rp FROM RestricaoPerfil rp WHERE rp.id.idPerfil = :idPerfil AND rp.id.idRestricao = :idRestricao")
    RestricaoPerfil findRestricaoPerfilById(@Param("idPerfil") Long idPerfil, @Param("idRestricao") Long idRestricao);

    List<RestricaoPerfil> findByPerfil(Perfil perfil);

    List<RestricaoPerfil> findByRestricao(Restricao restricao);
}
