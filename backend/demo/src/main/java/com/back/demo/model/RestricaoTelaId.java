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
public class RestricaoTelaId implements Serializable {

    private Long idPerfil;
    private Long idTela;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RestricaoTelaId)) return false;
        RestricaoTelaId that = (RestricaoTelaId) o;
        return Objects.equals(idPerfil, that.idPerfil) &&
               Objects.equals(idTela, that.idTela);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPerfil, idTela);
    }
}
