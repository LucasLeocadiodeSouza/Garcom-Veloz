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
@Table(name = "perfil")
public class Perfil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descricao", length = 20)
    private String descricao;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean ativo;

    @Column(name = "criado_em")
    private LocalDate criadoEm;

    @OneToMany(mappedBy = "perfil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestricaoPerfil> restricaoPerfil;
}
