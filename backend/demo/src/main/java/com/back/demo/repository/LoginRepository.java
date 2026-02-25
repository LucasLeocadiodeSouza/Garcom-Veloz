package com.back.demo.repository;

import com.back.demo.model.Login;
import com.back.demo.model.LoginId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginRepository extends JpaRepository<Login, LoginId> {

    Login findByName(String name);

    Login findByUsuarioId(Long usuarioId);
}
