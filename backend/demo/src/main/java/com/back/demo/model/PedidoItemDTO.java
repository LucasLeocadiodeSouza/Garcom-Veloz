package com.back.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PedidoItemDTO {
    private Long       idPedido;
    private Long       idItem;
    private String     nomeItem;
    private String     descricaoItem;
    private Integer    quantidade;
    private BigDecimal valorItem;
}
