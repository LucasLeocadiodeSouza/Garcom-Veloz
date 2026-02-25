package com.back.demo.repository;

import com.back.demo.model.PedidoItem;
import com.back.demo.model.PedidoItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoItemRepository extends JpaRepository<PedidoItem, PedidoItemId> {

    @Query("SELECT pi FROM PedidoItem pi WHERE pi.id.idPedido = :idPedido AND pi.id.idItem = :idItem")
    PedidoItem findPedidoItemById(@Param("idPedido") Long idPedido, @Param("idItem") Long idItem);

    // List<PedidoItem> findByIdPedido(Long idPedido);

    // List<PedidoItem> findByIdItem(Long idItem);
}
