package com.back.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
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
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "estado")
    private Integer estado;

    @Column(name = "observacao", length = 255)
    private String observacao;

    @Column(name = "gorgeta", precision = 10, scale = 2)
    private BigDecimal gorgeta;

    @Column(name = "mesa")
    private Integer mesa;

    @ManyToOne
    @JoinColumn(name = "id_empresa", insertable = false, updatable = false)
    private Empresa empresa;

    @Column(name = "criado_em")
    private LocalDate criadoEm;

    @Column(name = "horario")
    private LocalTime horario;

    @Column(name = "ideusu", length = 20)
    private String ideusu;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoItem> itens;
}
