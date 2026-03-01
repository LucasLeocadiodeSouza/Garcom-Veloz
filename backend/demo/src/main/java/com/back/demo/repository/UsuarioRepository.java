package com.back.demo.repository;

import com.back.demo.model.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT user FROM Usuario user WHERE user.id = :id")
    Usuario findUsuarioById(@Param("id") Long id);

    Usuario findByTelefone(String telefone);

    List<Usuario> findAllByEmail(String email);

    List<Usuario> findAllUsuarioByAtivo(Boolean ativo);
}
