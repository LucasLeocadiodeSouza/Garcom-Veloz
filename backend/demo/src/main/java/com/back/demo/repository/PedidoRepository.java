package com.back.demo.repository;

import com.back.demo.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT pedido FROM Pedido pedido WHERE pedido.id = :id")
    Pedido findPedidoById(@Param("id") Long id);

    List<Pedido> findByEstado(Integer estado);

    List<Pedido> findByMesa(Integer mesa);

    @Query("SELECT p FROM Pedido p WHERE p.criadoEm BETWEEN :dataInicio AND :dataFim")
    List<Pedido> findByPeriodo(@Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);

    @Query("SELECT p FROM Pedido p WHERE p.estado = :estado AND p.criadoEm BETWEEN :dataInicio AND :dataFim")
    List<Pedido> findByEstadoAndPeriodo(@Param("estado") Integer estado, @Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
}
