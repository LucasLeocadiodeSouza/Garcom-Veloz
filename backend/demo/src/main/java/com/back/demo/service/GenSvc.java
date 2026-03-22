package com.back.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.back.demo.exception.LoginNotFoundException;
import com.back.demo.exception.PerfilNotFoundException;
import com.back.demo.exception.RestricaoException;
import com.back.demo.exception.RestricaoNotFoundException;
import com.back.demo.exception.UsuarioException;
import com.back.demo.infra.security.TokenService;
import com.back.demo.model.EstatisticasDTO;
import com.back.demo.model.Login;
import com.back.demo.model.Perfil;
import com.back.demo.model.Restricao;
import com.back.demo.model.RestricaoPerfil;
import com.back.demo.model.RestricaoTela;
import com.back.demo.model.FormTela;
import com.back.demo.repository.CategoriaRepository;
import com.back.demo.repository.EstatisticasDTORepository;
import com.back.demo.repository.FormTelaRepository;
import com.back.demo.repository.ItemRepository;
import com.back.demo.repository.LoginRepository;
import com.back.demo.repository.RestricaoPerfilRepository;
import com.back.demo.repository.RestricaoRepository;
import com.back.demo.repository.RestricaoTelaRepository;
import com.back.demo.repository.UsuarioRepository;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class GenSvc {

    @Autowired
    private EstatisticasDTORepository estatisticasDTORepo; 

    @Autowired
    private UsuarioRepository usuarioRepo;
    
    @Autowired
    private LoginRepository loginRepo;

    @Autowired
    private CategoriaRepository categoriaRepo; 

    @Autowired
    private ItemRepository itemRepo;

    @Autowired
    private FormTelaRepository formTelaRepo;

    @Autowired
    private RestricaoTelaRepository restTelaRepo;

    @Autowired
    private RestricaoPerfilRepository restricaoPerfilRepo;

    @Autowired
    private RestricaoRepository restricaoRepo;

    @Autowired
    private TokenService tokenService;


    public String recuperarToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;

        return authHeader.replace("Bearer ", "").trim();
    }

    public String getUserName(HttpServletRequest request) {
        var auth = recuperarToken(request);
        
        if(auth != null) return tokenService.getExtractedUsernameFromToken(auth);

        //Cookie[] cookies = request.getCookies();
        //if(cookies != null){
        //    for(Cookie cookie : cookies){
        //        if("authToken".equals(cookie.getName())){
        //            String token = cookie.getValue();
        //            String username = tokenService.getExtractedUsernameFromToken(token);
        //            return username;
        //        }
        //    }
        //}
        return "";
    }

    public Boolean validarAutenticacao(HttpServletRequest request){
        String token = recuperarToken(request);

        if (token != null) {
            String subject = tokenService.validarToken(token);
            return subject != null && !subject.isBlank();
        }

        return false;
    }

    public String getNomeUsuarioByIdeusu(String ideusu){
        Login loginIdeusu = loginRepo.findByName(ideusu);
        if(loginIdeusu != null) return loginIdeusu.getName();

        return "";
    }

    public void validaUsuarioByIdeusu(String ideusu){
        Login loginIdeusu = loginRepo.findByName(ideusu);

        if(loginIdeusu == null) throw new LoginNotFoundException("Usuário informado não encontrado ");
    }

    public Long getCodEmpresaByIdeusu(String ideusu){
        Login loginIdeusu = loginRepo.findByName(ideusu);

        if(loginIdeusu == null) throw new LoginNotFoundException("Usuário informado não encontrado.");

        if(loginIdeusu.getUsuario() == null) throw new UsuarioException("Usuário não vinculado para o login.");
            
        return loginIdeusu.getUsuario().getEmpresa().getId();
    }

    public Perfil getPerfilUsuario(String ideusu){
        Login loginIdeusu = loginRepo.findByName(ideusu);
        if(loginIdeusu == null) throw new LoginNotFoundException("Usuário informado não encontrado.");

        Perfil perfil = loginIdeusu.getUsuario().getPerfil();
        if(perfil == null) throw new PerfilNotFoundException("Perfil do usuário informado não encontrado.");

        return perfil;
    }

    public EstatisticasDTO getStatsHome(){
        EstatisticasDTO estatisticas = new EstatisticasDTO();

        estatisticas.setTotalCategorias(categoriaRepo.countAllCategoria());
        estatisticas.setTotalUsuariosAtivos(usuarioRepo.countAllUsuarioByStatus(true));
        estatisticas.setTotalprodutos(itemRepo.countAllItens());
        estatisticas.setTotalExportacoes(0L);

        estatisticas.setProdutoMes(estatisticasDTORepo.getCountRegisterByDate("Item", 30));
        estatisticas.setExportacaoSemana(estatisticasDTORepo.getCountRegisterByDate("Item", 7));
        estatisticas.setUsuarioMes(estatisticasDTORepo.getCountRegisterByDate("Usuario", 30));
        estatisticas.setCategoriaSemana(estatisticasDTORepo.getCountRegisterByDate("Categoria", 7));

        return estatisticas;
    }



    public String formataTelefoneBanco(String telefone){
        if(telefone.contains("(")) telefone = telefone.replace("(", "");
        if(telefone.contains(")")) telefone = telefone.replace(")", "");
        if(telefone.contains(" ")) telefone = telefone.replace(" ", "");
        if(telefone.contains("-")) telefone = telefone.replace("-", "");

        return telefone;
    }

    public String[] getAllCorCategoria(){
        return new String[]{"#3b82f6", "#10b981", "#f59e0b", "#ef4444", "#8b5cf6", "#ec4899", "#14b8a6", "#f97316"};
    }

    public String[] getAllIconeCategoria(){
        return new String[]{"🍽️", "🥤", "🍔", "🍕", "🥗", "🍰", "🍷", "🥩", "🌮", "🍜", "🍣", "🧃"};
    }

    public List<FormTela> getFormTelas() {
        return formTelaRepo.findAllOrderedByLabel();
    }

    public List<FormTela> getFormTelasByPerfilUsu(String ideusu) {
        Login loginIdeusu = loginRepo.findByName(ideusu);
        if(loginIdeusu == null) throw new LoginNotFoundException("Usuário informado não encontrado ");

        List<RestricaoTela> restricoes = restTelaRepo.findAtivasByIdPerfil(loginIdeusu.getUsuario().getPerfil().getId());
        
        List<FormTela> telasForm = formTelaRepo.findAllOrderedByLabel();

        for(RestricaoTela restricao : restricoes){
            if(telasForm.contains(restricao.getTela())) telasForm.remove(restricao.getTela());
        }

        return telasForm;
    }

    // #################### Permissões de Perfil ####################

    public void usuarioTemPermissao(String restricao, String ideusu){
        Login loginIdeusu = loginRepo.findByName(ideusu);

        Restricao rest = restricaoRepo.findRestricaoByDescricao(restricao);
        if(rest == null) throw new RestricaoNotFoundException("Não encontrado a restrição informada");

        Perfil perfilUsuario = loginIdeusu.getUsuario().getPerfil();
        if(perfilUsuario == null) throw new PerfilNotFoundException("Não encontrado o perfil do usuário informado");

        RestricaoPerfil restPerfil = restricaoPerfilRepo.findRestricaoPerfilActiveById(perfilUsuario.getId(), rest.getId());
        if(restPerfil == null) throw new RestricaoException("Usuário não possui permissão para executar a ação");
    }
}
