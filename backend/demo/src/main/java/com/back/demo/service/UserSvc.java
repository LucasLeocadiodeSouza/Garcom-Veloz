package com.back.demo.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.back.demo.exception.UsuarioException;
import com.back.demo.exception.UsuarioNotFoundException;
import com.back.demo.model.Login;
import com.back.demo.model.Usuario;
import com.back.demo.repository.LoginRepository;
import com.back.demo.repository.UsuarioRepository;

@Service
public class UserSvc implements UserDetailsService  {
    
    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private GenSvc genSvc;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loginRepository.findByName(username);
    }

    public Integer getEmpresaUsuario(Long id, String login){
        if(id != null && id != Long.valueOf(0)){
            Usuario usuario = usuarioRepo.findUsuarioById(id);
            if(usuario == null) return 0;

            return usuario.getEmpresa();
        }

        if(login != null && !login.isBlank()){
            Login userAcess = loginRepository.findByName(login);
            if(userAcess == null) return 0;

            Usuario usuario = userAcess.getUsuario();
            if(usuario == null) throw new UsuarioNotFoundException(login);

            return usuario.getEmpresa();
        }

        return 0;
    }

    public void criarAlterarUsuario(Long    id,
                                    String  nome, 
                                    String  email, 
                                    String  telefone, 
                                    Integer empresa)
                                    {        

        if(nome == null || nome.isBlank()) throw new UsuarioException("É preciso informar o nome do usuário para continuar");
        //if(email == null || email.isBlank()) throw new UsuarioException("É preciso informar o email do usuário para continuar");
        //if(telefone == null || telefone.isBlank()) throw new UsuarioException("É preciso informar o telefone do usuário para continuar");
        if(empresa == null || empresa == 0) throw new UsuarioException("É preciso informar a empresa vinculada ao usuário para continuar");

        if(genSvc.formataTelefoneBanco(telefone).length() != 11) throw new UsuarioException("Vincule um número valido para cadastrar o usuário");

        Usuario usuario = usuarioRepo.findUsuarioById(id);

        if(usuario == null){
            usuario = new Usuario();
            usuario.setCriadoEm(LocalDate.now());
        }

        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setTelefone(genSvc.formataTelefoneBanco(telefone));
        usuario.setEmpresa(empresa);

        usuarioRepo.save(usuario);
    }
}
