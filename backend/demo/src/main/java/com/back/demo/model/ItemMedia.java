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
@Table(name = "item_media")
@IdClass(ItemMediaId.class)
public class ItemMedia {

    @Id
    @Column(name = "id_item")
    private Long idItem;

    @Id
    @Column(name = "seq")
    private Integer seq;

    @ManyToOne
    @JoinColumn(name = "id_item", insertable = false, updatable = false)
    private Item item;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "criado_em")
    private LocalDate criadoEm;

    @Column(name = "ideusu", length = 20)
    private String ideusu;
}
