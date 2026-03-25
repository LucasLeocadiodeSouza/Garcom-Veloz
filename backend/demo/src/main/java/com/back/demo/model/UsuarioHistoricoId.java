package com.back.demo.model;

import java.io.Serializable;
import java.util.Objects;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UsuarioHistoricoId implements Serializable {

    private Long idUsuario;
    private Long seq;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UsuarioHistoricoId))
            return false;
        UsuarioHistoricoId that = (UsuarioHistoricoId) o;
        return Objects.equals(idUsuario, that.idUsuario) && Objects.equals(seq, that.seq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, seq);
    }
}
