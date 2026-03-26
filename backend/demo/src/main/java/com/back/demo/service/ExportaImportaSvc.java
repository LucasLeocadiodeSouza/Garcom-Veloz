package com.back.demo.service;

import java.math.BigDecimal;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.back.demo.model.ItemDTO;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import com.back.demo.model.ItemImportDTO;
import com.back.demo.model.Categoria;
import com.back.demo.repository.CategoriaRepository;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

@Service
public class ExportaImportaSvc {
    
    @Autowired
    private ItemSvc itemSvc;

    @Autowired
    private GenSvc gensvc;

    @Autowired
    private CategoriaRepository categoriaRepo;

    public List<Map<String, String>> previewImportacao(MultipartFile file) throws Exception {
        List<Map<String, String>> rows = new ArrayList<>();
        
        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                     .setDelimiter(';')
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setIgnoreHeaderCase(true)
                     .setTrim(true)
                     .build())) {
            
            List<String> headers = csvParser.getHeaderNames();
            for (CSVRecord csvRecord : csvParser) {
                Map<String, String> rowData = new java.util.LinkedHashMap<>();
                for (String header : headers) {
                    rowData.put(header, csvRecord.isSet(header) ? csvRecord.get(header) : "");
                }
                rows.add(rowData);
            }
            return rows;
        } catch (Exception ex) {
            throw new Exception("Falha ao ler o arquivo CSV. Certifique-se de que é um CSV válido separado por ponto e vírgula (;).");
        }
    }

    public void importarProdutos(List<ItemImportDTO> itens, String ideusu) {
        for (ItemImportDTO dto : itens) {
            Long categId = dto.getIdCategoria();
            
            // Check category
            if (categId == null || categId <= 0) {
                String categNome = dto.getCategoria() != null && !dto.getCategoria().isBlank() ? dto.getCategoria() : "Importada";

                List<Categoria> matchs = categoriaRepo.findCategoriaByDescricao(categNome);
                if (matchs != null && !matchs.isEmpty()) categId = matchs.get(0).getId();
                else {
                    itemSvc.criarAlterarCategoria(null, categNome, "", "", null, ideusu);
                    List<Categoria> newCategs = categoriaRepo.findCategoriaByDescricao(categNome);
                    if (newCategs != null && !newCategs.isEmpty()) categId = newCategs.get(0).getId();
                }
            }
            
            BigDecimal desconto = dto.getDesconto() != null ? dto.getDesconto() : BigDecimal.ZERO;
            Boolean ativo       = dto.getAtivo() != null ? dto.getAtivo() : true;
            Integer estoque     = dto.getEstoque() != null ? dto.getEstoque() : 0;

            itemSvc.criarAlterarItem(
                dto.getId(),
                dto.getNome(),
                "",
                dto.getVlrItem(),
                desconto,
                estoque,
                categId,
                null,
                ideusu
            );
            
            // Ativar or desativar based on status 
            if (dto.getId() != null) {
                try {
                    itemSvc.ativarInativarItem(dto.getId(), ativo, ideusu);
                } catch(Exception e) {}
            }
        }
    }

    public void exportarProdutos(String status, java.io.Writer writer) throws java.io.IOException {
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, 
                                                    CSVFormat.DEFAULT.builder()
                                                        .setDelimiter(';')
                                                        .setHeader("id", "nome", "ativo", "idCategoria", "categoria", "estoque", "vlr item", "desconto", "valor liq", "data", "ideusu")
                                                        .build()
                                                    )
            ){

            List<ItemDTO> itensDTO = itemSvc.getListItem("", status, null);

            for (ItemDTO itemDTO : itensDTO) {
                BigDecimal vlrliquido = itemDTO.getValor().subtract(itemDTO.getDesconto());

                csvPrinter.printRecord(
                    itemDTO.getIdItem(),
                    itemDTO.getNome(),
                    itemDTO.getAtivo(),
                    itemDTO.getIdCategoria(),
                    itemDTO.getCategDecricao(),
                    itemDTO.getEstoque(),
                    itemDTO.getValor(),
                    itemDTO.getDesconto(),
                    vlrliquido,
                    "",
                    ""
                );
            }
        }
    }

}
