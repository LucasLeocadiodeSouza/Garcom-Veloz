package com.back.demo.repository;

import com.back.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT user FROM Usuario user WHERE user.id = :id")
    Usuario findUsuarioById(@Param("id") Long id);

    Optional<Usuario> findByTelefone(String telefone);

    Optional<Usuario> findByEmail(String email);
}
