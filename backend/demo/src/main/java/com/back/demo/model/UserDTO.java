package com.back.demo.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDTO {
    private Long      idUsuario;
    private String    nome;
    private String    email;
    private String    telefone;
    private Long      perfId;
    private String    perfDescricao;
    private Boolean   status;
    private Long      idEmpresa;
    private LocalDate criadoEm;

    private Long    restId;
    private String  restDescricao;

    // Identificacao menu
    public UserDTO(Long idUsuario, 
                   String nome, 
                   String perfDescricao) 
                   {
        this.idUsuario     = idUsuario;
        this.nome          = nome;
        this.perfDescricao = perfDescricao;
    }

    public UserDTO(Long      idUsuario, 
                   String    nome, 
                   String    email, 
                   String    telefone,
                   Long      perfId, 
                   String    perfDescricao, 
                   Boolean   status,
                   Long      idEmpresa,
                   LocalDate criadoEm)
                   {
        this.idUsuario     = idUsuario;
        this.nome          = nome;
        this.email         = email;
        this.telefone      = telefone;
        this.perfId        = perfId;
        this.perfDescricao = perfDescricao;
        this.status        = status;
        this.idEmpresa     = idEmpresa;
        this.criadoEm      = criadoEm;
    }

    public UserDTO(Long   perfId, 
                   String perfDescricao, 
                   Long   restId, 
                   String restDescricao) 
                   {
        this.perfId        = perfId;
        this.perfDescricao = perfDescricao;
        this.restId        = restId;
        this.restDescricao = restDescricao;
    }
    
    
}
