package com.back.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.back.demo.model.Perfil;
import com.back.demo.model.UserDTO;
import com.back.demo.service.GenSvc;
import com.back.demo.service.UserSvc;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/restrictedApi")
public class RestrictedApi {

    @Autowired
    private UserSvc userService;

    @Autowired
    private GenSvc genSvc;


    @GetMapping("/teste")
    private String getTeste() {
        return "teste";
    }

    @GetMapping("/getListUsers")
    private List<UserDTO> getUsuarioGrid(@RequestParam(name  = "search", required   = false) String search,
                                         @RequestParam(value = "idPerfil", required = false) Long idPerfil,
                                         @RequestParam(value = "status", required   = false) String status,
                                         HttpServletRequest request){
        String ideusu = genSvc.getUserName(request);

        return userService.getListUsers(search, status, genSvc.getCodEmpresaByIdeusu(ideusu), idPerfil);
    }

    @GetMapping("/getAllPerfil")
    private List<Perfil> getAllPerfil() {
        return userService.getAllPerfil();
    }

    @GetMapping("/getAllRestricoesPerfil")
    private List<UserDTO> getAllRestricoesPerfil() {
        return userService.getAllRestricoesPerfil();
    }

    @PostMapping("/criarAlterarUsuario")
    private ResponseEntity<?> criarAlterarUsuario(@RequestBody UserDTO dto, HttpServletRequest request) {
        String ideusu = genSvc.getUserName(request);

        userService.criarAlterarUsuario(dto.getIdUsuario(),
                                        dto.getNome(),
                                        dto.getEmail(),
                                        dto.getTelefone(),
                                        dto.getPerfId(),
                                        genSvc.getCodEmpresaByIdeusu(ideusu),
                                        ideusu);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Usuário Criado/Alterado com sucesso"));
    }

    @PostMapping("/ativarInativarUsuario")
    private ResponseEntity<?> ativarInativarUsuario(@RequestParam(name = "idUsuario", required = true) Long idUsuario,
                                                    @RequestParam(name = "ativar", required = true) Boolean ativar, 
                                                    HttpServletRequest request) {
        userService.ativarInativarUsuario(idUsuario, ativar, genSvc.getUserName(request));

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Usuário ativado/desativado com sucesso"));
    }
}
