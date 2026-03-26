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
public class ItemImportDTO {
    private Long       id;
    private String     nome;
    private Boolean    ativo;
    private Long       idCategoria;
    private String     categoria;
    private Integer    estoque;
    private BigDecimal vlrItem;
    private BigDecimal desconto;
    private BigDecimal valorLiq;
}
