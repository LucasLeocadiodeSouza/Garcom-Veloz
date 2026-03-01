package com.back.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDTO {
    private Long    idUsuario;
    private String  nome;
    private String  email;
    private Long    perfId;
    private String  perfDescricao;
    private String  status;
    private Long    idEmpresa;

    private Long    totalResult;
    private Long    totalAtivos;
    private Long    totalInativos;

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

    public UserDTO(Long   idUsuario, 
                   String nome, 
                   String email, 
                   Long   perfId, 
                   String perfDescricao, 
                   String status,
                   Long   idEmpresa,
                   Long   totalResult, 
                   Long   totalAtivos, 
                   Long   totalInativos)
                   {
        this.idUsuario     = idUsuario;
        this.nome          = nome;
        this.email         = email;
        this.perfId        = perfId;
        this.perfDescricao = perfDescricao;
        this.status        = status;
        this.totalResult   = totalResult;
        this.totalAtivos   = totalAtivos;
        this.totalInativos = totalInativos;
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
