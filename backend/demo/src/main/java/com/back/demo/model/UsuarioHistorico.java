package com.back.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "usuario_historico")
public class UsuarioHistorico {

    @EmbeddedId
    private UsuarioHistoricoId id;

    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "label", length = 75)
    private String label;

    @Column(name = "criado_em")
    private LocalDate criadoEm;

    @Column(name = "horario")
    private LocalTime horario;
}
