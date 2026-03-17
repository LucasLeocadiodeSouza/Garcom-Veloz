package com.back.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.back.demo.infra.security.TokenService;
import com.back.demo.model.Login;
import com.back.demo.model.securityLogin.AuthenticationDTO;
import com.back.demo.model.securityLogin.LoginResponseDTO;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthApi {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO data, HttpServletResponse response) {
        var userNamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.passkey());
        var auth = this.authenticationManager.authenticate(userNamePassword);
        var token = tokenService.gerarToken((Login) auth.getPrincipal());

        Cookie cookie = new Cookie("authToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/"); //disponivel em todo dominio
        cookie.setMaxAge(1 * 10 * 60 * 60); //expira em 10 horas
        response.addCookie(cookie);

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }   

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("authToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); //expira imaediatamente
        response.addCookie(cookie);

        return ResponseEntity.ok("OK");
    }  
}
