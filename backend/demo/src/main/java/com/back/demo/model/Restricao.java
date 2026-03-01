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
@Table(name = "restricao")
public class Restricao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descricao", length = 20)
    private String descricao;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean ativo;

    @Column(name = "criado_em")
    private LocalDate criadoEm;

    @OneToMany(mappedBy = "restricao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestricaoPerfil> restricaoPerfil;
}
