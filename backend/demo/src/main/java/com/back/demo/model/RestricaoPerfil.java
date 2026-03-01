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
@Table(name = "restricao_perfil")
public class RestricaoPerfil {
    
    @EmbeddedId
    private RestricaoPerfilId id;

    @ManyToOne
    @MapsId("idPerfil")
    @JoinColumn(name = "id_perfil", insertable = false, updatable = false)
    private Perfil perfil;

    @ManyToOne
    @MapsId("idRestricao")
    @JoinColumn(name = "id_restricao", insertable = false, updatable = false)
    private Restricao restricao;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean ativo;

    @Column(name = "criado_em")
    private LocalDate criadoEm;
}
