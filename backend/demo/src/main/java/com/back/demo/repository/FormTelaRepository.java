package com.back.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.back.demo.model.FormTela;

public interface FormTelaRepository extends JpaRepository<FormTela, Long> {

    @Query("SELECT t FROM FormTela t ORDER BY t.label")
    List<FormTela> findAllOrderedByLabel();
}
