package com.back.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.back.demo.infra.security.TokenService;
import com.back.demo.model.EstatisticasDTO;
import com.back.demo.model.Login;
import com.back.demo.model.Usuario;
import com.back.demo.repository.CategoriaRepository;
import com.back.demo.repository.EstatisticasDTORepository;
import com.back.demo.repository.ItemRepository;
import com.back.demo.repository.LoginRepository;
import com.back.demo.repository.UsuarioRepository;
import jakarta.servlet.http.Cookie;
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
    private TokenService tokenService;


    public String getUserName(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if("authToken".equals(cookie.getName())){
                    String token = cookie.getValue();
                    String username = tokenService.getExtractedUsernameFromToken(token);
                    return username;
                }
            }
        }
        return "";
    }

    public String getNomeUsuarioByIdeusu(String ideusu){
        Login loginIdeusu = loginRepo.findByName(ideusu);
        if(loginIdeusu != null) return loginIdeusu.getName();

        return "";
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
}
