package com.back.demo.repository;

import com.back.demo.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT pedido FROM Pedido pedido WHERE pedido.id = :id")
    Pedido findPedidoById(@Param("id") Long id);

    List<Pedido> findByEstado(Integer estado);

    List<Pedido> findByMesa(Integer mesa);
}
