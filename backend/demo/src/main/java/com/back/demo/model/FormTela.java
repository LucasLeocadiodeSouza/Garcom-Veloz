package com.back.demo.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "form_tela")
public class FormTela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "label", length = 30)
    private String label;

    @Column(name = "router", length = 100)
    private String router;

    @Column(name = "svg", length = 400)
    private String svg;

    @Column(name = "admtype", columnDefinition = "TINYINT(1)")
    private Boolean admtype;
}
