package com.back.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descricao", length = 60)
    private String descricao;

    @Column(name = "referencia_ext")
    private Long refereciaExt;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean ativo;

    @Column(name = "criado_em")
    private LocalDate criadoEm;

    @Column(name = "ideusu", length = 20)
    private String ideusu;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> itens;
}
