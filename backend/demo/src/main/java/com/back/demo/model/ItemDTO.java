package com.back.demo.model;

import java.math.BigDecimal;
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
    private String     decricao;
    private BigDecimal valor;
    private BigDecimal desconto;
    private Integer    estoque;
    private Boolean    ativo;

    private Long       totalResult;
    private Long       totalAtivo;
    private Long       totalInativo;

    private List<String> mediaPath;

    // Grid Principal tela Produtos
    public ItemDTO(Long idItem, 
                   Long idCategoria, 
                   String categDecricao, 
                   String nome, 
                   String decricao, 
                   BigDecimal valor,
                   BigDecimal desconto, 
                   Integer estoque, 
                   Boolean ativo, 
                   Long    totalResult, 
                   Long    totalAtivo,
                   Long    totalInativo) 
                   {
        this.idItem        = idItem;
        this.idCategoria   = idCategoria;
        this.categDecricao = categDecricao;
        this.nome          = nome;
        this.decricao      = decricao;
        this.valor         = valor;
        this.desconto      = desconto;
        this.estoque       = estoque;
        this.ativo         = ativo;
        this.totalResult   = totalResult;
        this.totalAtivo    = totalAtivo;
        this.totalInativo  = totalInativo;
    }

    // Tela de cardapio
    public ItemDTO(String nome, 
                   String decricao, 
                   BigDecimal valor, 
                   BigDecimal desconto, 
                   List<String> mediaPath) {
        this.nome      = nome;
        this.decricao  = decricao;
        this.valor     = valor;
        this.desconto  = desconto;
        this.mediaPath = mediaPath;
    }

    
    
}
