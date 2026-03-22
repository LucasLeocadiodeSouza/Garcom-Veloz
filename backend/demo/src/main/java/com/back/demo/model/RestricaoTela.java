package com.back.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "restricao_tela")
public class RestricaoTela {

    @EmbeddedId
    private RestricaoTelaId id;

    @ManyToOne
    @MapsId("idPerfil")
    @JoinColumn(name = "id_perfil", insertable = false, updatable = false)
    private Perfil perfil;

    @ManyToOne
    @MapsId("idTela")
    @JoinColumn(name = "id_tela", insertable = false, updatable = false)
    private FormTela tela;

    @Column(columnDefinition = "TINYINT")
    private Integer ativo;

    @Column(name = "criado_em")
    private LocalDate criadoEm;
}
