package com.back.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import com.back.demo.infra.security.TokenService;
import com.back.demo.model.Login;
import com.back.demo.model.securityLogin.AuthenticationDTO;
import com.back.demo.model.securityLogin.LoginResponseDTO;
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

        // Cookie cookie = new Cookie("authToken", token);
        // cookie.setHttpOnly(true);
        // cookie.setSecure(true);
        // cookie.setPath("/"); //disponivel em todo dominio
        // cookie.setMaxAge(1 * 10 * 60 * 60); //expira em 10 horas
        // response.addCookie(cookie);

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }   

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Cookie cookie = new Cookie("authToken", "");
        // cookie.setHttpOnly(true);
        // cookie.setSecure(true);
        // cookie.setPath("/");
        // cookie.setMaxAge(0); //expira imaediatamente
        // response.addCookie(cookie);


        return ResponseEntity.ok(
            Map.of(
                "logout",
                "sucess"
            )
        );
    }  
    
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null ||
            !authentication.isAuthenticated() ||
            authentication.getPrincipal().equals("anonymousUser")) {

            System.out.println(authentication == null);
            System.out.println("==================");
            System.out.println(!authentication.isAuthenticated());
            System.out.println("==================");
            System.out.println(authentication.getPrincipal().equals("anonymousUser"));

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        System.out.println("======================");

        UserDetails user = (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(
            Map.of(
                "username", user.getUsername(),
                "roles", user.getAuthorities()
            )
        );
    }
}
