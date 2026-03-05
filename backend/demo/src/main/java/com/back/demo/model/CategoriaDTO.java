package com.back.demo.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {
    private Long    idCategoria;
    private String  descricao;
    private String  icone;
    private String  cor;
    private Boolean status;
    private LocalDate criadoEm;
    private Long    totalProdVinc;
    private Long    totalCategAtivo;
    private Long    totalCategInativo;



    // Grid de Categorias
    public CategoriaDTO(Long    idCategoria,
                        String  descricao, 
                        String  icone, 
                        String  cor, 
                        Boolean status,
                        Long    totalProdVinc,
                        LocalDate criadoEm)
                        {
        this.idCategoria   = idCategoria;
        this.descricao     = descricao;
        this.icone         = icone;
        this.cor           = cor;
        this.status        = status;
        this.totalProdVinc = totalProdVinc;
        this.criadoEm      = criadoEm;
    }


    // Stats Categoria
    public CategoriaDTO(Long totalProdVinc,
                        Long totalCategAtivo, 
                        Long totalCategInativo)
                        {
        this.totalProdVinc     = totalProdVinc;
        this.totalCategAtivo   = totalCategAtivo;
        this.totalCategInativo = totalCategInativo;
    }

    
}
