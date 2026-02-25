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
public class ItemMediaId implements Serializable {

    private Long idItem;
    private Integer seq;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ItemMediaId))
            return false;
        ItemMediaId that = (ItemMediaId) o;
        return Objects.equals(idItem, that.idItem) && Objects.equals(seq, that.seq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idItem, seq);
    }
}
