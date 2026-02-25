package com.back.demo.model;

import java.io.Serializable;
import java.util.Objects;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PedidoItemId implements Serializable {

    private Long idPedido;
    private Long idItem;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PedidoItemId))
            return false;
        PedidoItemId that = (PedidoItemId) o;
        return Objects.equals(idPedido, that.idPedido) && Objects.equals(idItem, that.idItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPedido, idItem);
    }
}
