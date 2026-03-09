package com.back.demo.repository;

import java.time.LocalDate;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;

@Repository
public class EstatisticasDTORepository {
    private final EntityManager em;

    public EstatisticasDTORepository(EntityManager em){
        this.em = em;
    }

    public Long getCountRegisterByDate(String tabela, Integer date){
        LocalDate fim = LocalDate.now();
        LocalDate inicio = fim.minusDays(date);

        String query = "SELECT COUNT(r) FROM " + tabela + " r WHERE r.criadoEm BETWEEN :inicio AND :fim";

        var q = em.createQuery(query, Long.class);

        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);

        return q.getSingleResult();
    }
}
