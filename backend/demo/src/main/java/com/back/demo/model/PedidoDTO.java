package com.back.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PedidoDTO {
    private Long                id;
    private Integer             estado;
    private String              descEstado;
    private String              observacao;
    private BigDecimal          gorgeta;
    private Integer             mesa;
    private LocalDate           criadoEm;
    private LocalTime           horario;
    private String              ideusu;
    private BigDecimal          valorTotal;
    private List<PedidoItemDTO> itens;
}
