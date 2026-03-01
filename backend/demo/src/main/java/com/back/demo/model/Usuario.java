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
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", length = 60)
    private String nome;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "telefone", length = 13, nullable = false, unique = true)
    private String telefone;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean ativo;

    @Column(name = "empresa")
    private Integer empresa;

    @Column(name = "criado_em")
    private LocalDate criadoEm;

    @ManyToOne
    @JoinColumn(name = "id_perfil", insertable = false, updatable = false)
    private Perfil perfil;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Login> logins;
}
