package com.back.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EstatisticasDTO {
    private Long totalprodutos;
    private Long totalUsuariosAtivos;
    private Long totalExportacoes;
    private Long totalCategorias;

    private Long produtoMes;
    private Long usuarioMes;
    private Long exportacaoSemana;
    private Long categoriaSemana;
}
