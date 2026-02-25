package com.back.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "pedido_item")
public class PedidoItem {

    @EmbeddedId
    private PedidoItemId id;

    // @Id
    // @Column(name = "id_pedido")
    // private Long idPedido;

    // @Id
    // @Column(name = "id_item")
    // private Long idItem;

    @ManyToOne
    @MapsId("idPedido")
    @JoinColumn(name = "id_pedido", insertable = false, updatable = false)
    private Pedido pedido;

    @ManyToOne
    @MapsId("idItem")
    @JoinColumn(name = "id_item", insertable = false, updatable = false)
    private Item item;

    @Column(name = "criado_em")
    private LocalDate criadoEm;

    @Column(name = "ideusu", length = 20)
    private String ideusu;
}
