package com.back.demo.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemDTO {
    private Long       idItem;
    private Long       idCategoria;
    private String     categDecricao;
    private String     nome;
    private String     descricao;
    private BigDecimal valor;
    private BigDecimal desconto;
    private Integer    estoque;
    private Boolean    ativo;
    private LocalDate  date;
    private String     ideusu;
    private Long       idItemRef;

    private List<String> mediaPath;

    // Grid Principal tela Produtos
    public ItemDTO(Long       idItem, 
                   Long       idCategoria, 
                   String     categDecricao, 
                   String     nome, 
                   String     descricao, 
                   BigDecimal valor,
                   BigDecimal desconto, 
                   Integer    estoque, 
                   Boolean    ativo, 
                   LocalDate  date, 
                   String     ideusu,
                   Long       idItemRef) 
                   {
        this.idItem        = idItem;
        this.idCategoria   = idCategoria;
        this.categDecricao = categDecricao;
        this.nome          = nome;
        this.descricao     = descricao;
        this.valor         = valor;
        this.desconto      = desconto;
        this.estoque       = estoque;
        this.ativo         = ativo;
        this.date          = date;
        this.ideusu        = ideusu;
        this.idItemRef     = idItemRef;
    }

    // Tela de cardapio
    public ItemDTO(String nome, 
                   String descricao, 
                   BigDecimal valor, 
                   BigDecimal desconto, 
                   List<String> mediaPath) {
        this.nome      = nome;
        this.descricao  = descricao;
        this.valor     = valor;
        this.desconto  = desconto;
        this.mediaPath = mediaPath;
    }

    
    
}
