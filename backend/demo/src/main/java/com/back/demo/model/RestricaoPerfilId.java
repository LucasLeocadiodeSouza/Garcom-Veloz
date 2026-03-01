package com.back.demo.model;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RestricaoPerfilId implements Serializable {

    private Long idPerfil;
    private Long idRestricao;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RestricaoPerfilId))
            return false;
        RestricaoPerfilId that = (RestricaoPerfilId) o;
        return Objects.equals(idPerfil, that.idPerfil) && 
               Objects.equals(idRestricao, that.idRestricao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPerfil, idRestricao);
    }
}
