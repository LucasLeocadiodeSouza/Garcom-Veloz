package com.back.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.back.demo.model.Empresa;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long>{
    @Query("SELECT e FROM Empresa e WHERE e.id = :id")
    Empresa findEmpresaById(@Param("id") Long id);
}
