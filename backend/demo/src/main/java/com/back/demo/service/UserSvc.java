package com.back.demo.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.back.demo.exception.EmpresaNotFoundException;
import com.back.demo.exception.PerfilNotFoundException;
import com.back.demo.exception.UsuarioException;
import com.back.demo.exception.UsuarioNotFoundException;
import com.back.demo.model.Empresa;
import com.back.demo.model.Login;
import com.back.demo.model.Perfil;
import com.back.demo.model.UserDTO;
import com.back.demo.model.Usuario;
import com.back.demo.repository.EmpresaRepository;
import com.back.demo.repository.LoginRepository;
import com.back.demo.repository.PerfilRepository;
import com.back.demo.repository.RestricaoTelaRepository;
import com.back.demo.repository.UserDTORepository;
import com.back.demo.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

@Service
public class UserSvc implements UserDetailsService  {
    
    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private UserDTORepository userDTORepo;

    @Autowired
    private EmpresaRepository empresaRepo;

    @Autowired
    private PerfilRepository perfilRepo;

    @Autowired
    private GenSvc genSvc;

    @Autowired
    private RestricaoTelaRepository restricaoTelaRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { return loginRepository.findByName(username); }

    public Empresa getEmpresaUsuario(Long id, String login){
        if(id != null && id != Long.valueOf(0)){
            Usuario usuario = usuarioRepo.findUsuarioById(id);
            if(usuario == null) return null;

            return usuario.getEmpresa();
        }

        if(login != null && !login.isBlank()){
            Login userAcess = loginRepository.findByName(login);
            if(userAcess == null) return null;

            Usuario usuario = userAcess.getUsuario();
            if(usuario == null) throw new UsuarioNotFoundException(login);

            return usuario.getEmpresa();
        }

        return null;
    }


    public List<UserDTO> getListUsers(String nome, String ativo, Long idEmpresa, Long idPerfil){
        List<UserDTO> usuarios = userDTORepo.getListUsuarios(nome, ativo, idEmpresa, idPerfil);

        return usuarios;
    }

    public List<UserDTO> getAllRestricoesPerfil(){
        List<UserDTO> usuarios = userDTORepo.getAllRestricoesPerfil();
        return usuarios;
    }

    public List<Object[]> getAllRestricoesTela(){
        return restricaoTelaRepo.findAllFlat();
    }

    public List<Perfil> getAllPerfil(){
        List<Perfil> perfil = perfilRepo.findPerfilByStatus(true);

        return perfil;
    }

    @Transactional
    public void criarAlterarUsuario(Long    id,
                                    String  nome, 
                                    String  email, 
                                    String  telefone,
                                    Long    idPerfil,
                                    Long    idEmpresa,
                                    String  ideusu)
                                    {        

        Boolean ehNovoUsuario = false;

        if(nome == null || nome.isBlank()) throw new UsuarioException("É preciso informar o nome do usuário para continuar");
        //if(email == null || email.isBlank()) throw new UsuarioException("É preciso informar o email do usuário para continuar");
        if(email != null && !email.contains("@")) throw new UsuarioException("É preciso informar um email valido para continuar");
        //if(telefone == null || telefone.isBlank()) throw new UsuarioException("É preciso informar o telefone do usuário para continuar");
        if(idEmpresa == null || idEmpresa == 0) throw new UsuarioException("É preciso informar a empresa vinculada ao usuário para continuar");

        if(genSvc.formataTelefoneBanco(telefone).length() != 11) throw new UsuarioException("Vincule um número valido para cadastrar o usuário");

        Perfil perfil = perfilRepo.findPerfilById(idPerfil);
        if(perfil == null) throw new PerfilNotFoundException("Tipo de Perfil informado não encontrado!");

        Empresa empresa = empresaRepo.findEmpresaById(idEmpresa);
        if(empresa == null) throw new EmpresaNotFoundException("Empresa informada não encontrado!");

        Usuario usuario = usuarioRepo.findUsuarioById(id);

        if(usuario == null){
            usuario = new Usuario();
            usuario.setCriadoEm(LocalDate.now());
            usuario.setAtivo(true);
            usuario.setIdeusu(ideusu);
            ehNovoUsuario = true;
        }

        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setTelefone(genSvc.formataTelefoneBanco(telefone));
        usuario.setEmpresa(empresa);
        usuario.setPerfil(perfil);

        usuarioRepo.save(usuario);

        if(ehNovoUsuario){ criarAlterarLogin(usuario.getId(), ideusu); }
    }

    @Transactional
    public void criarAlterarLogin(Long idUsuario, String ideusu){
        //if(login == null || login.isBlank()) throw new UsuarioException("É preciso informar o login do usuário para continuar");
        //if(senha == null || senha.isBlank()) throw new UsuarioException("É preciso informar a senha do usuário para continuar");
        if(idUsuario == null || idUsuario == 0) throw new UsuarioException("É preciso informar o usuário vinculado ao login para continuar");

        Usuario usuario = usuarioRepo.findUsuarioById(idUsuario);
        if(usuario == null) throw new UsuarioNotFoundException("Não encontrado o usuário no sistema vinculado a empresa informada");

        //Login userLogin = loginRepository.findByName(login);

        if(loginRepository.findByUsuarioId(idUsuario) != null) throw new UsuarioException("Já existe uma conta para o usuário!");

        String loginName   = "";
        String[] nameCompl = usuario.getNome().split(" ");
        for (int i = 0; i < nameCompl.length || i < 2; i++) {
            String sobrenome = nameCompl[i];

            if(i == 0){
                loginName = nameCompl[0].length() < 9?nameCompl[0] : nameCompl[0].substring(0, 9);
            }else{
                if(sobrenome.equals("de") || sobrenome.equals("da") || sobrenome.equals("do")) continue;
                if(loginName.length() >= 20) break;

                loginName += sobrenome.substring(0, 1);
            }
        }

        Login userLogin = new Login();
        userLogin.setUsuario(usuario);
        userLogin.setName(loginName);
        userLogin.setPassword(new BCryptPasswordEncoder().encode("1234"));

        loginRepository.save(userLogin);
    }

    @Transactional
    public void ativarInativarUsuario(Long id, Boolean ativar, String ideusu){
        Usuario usuario = usuarioRepo.findUsuarioById(id);
        if(usuario == null) throw new UsuarioNotFoundException("Não encontrado o usuário no sistema vinculado a empresa informada");

        usuario.setAtivo(ativar);

        usuarioRepo.save(usuario);
    }


    // #################### Permissões de Perfil ####################

    // @Transactional
    // public void usuarioTemPermissao(Long id, Boolean ativar, String ideusu){
    //     Usuario usuario = usuarioRepo.findUsuarioById(id);
    //     if(usuario == null) throw new UsuarioNotFoundException("Não encontrado o usuário no sistema vinculado a empresa informada");

    //     usuario.setAtivo(ativar);

    //     usuarioRepo.save(usuario);
    // }
}
