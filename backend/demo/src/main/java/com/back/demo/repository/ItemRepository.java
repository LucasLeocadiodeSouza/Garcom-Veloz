package com.back.demo.repository;

import com.back.demo.model.Item;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT item FROM Item item WHERE item.id = :id")
    Item findItemById(@Param("id") Long id);
    
    @Query("SELECT item FROM Item item WHERE item.nome LIKE CONCAT('%', :nome, '%')")
    List<Item> findItemByNome(@Param("nome") String nome);

    @Query("SELECT item FROM Item item WHERE item.ativo = :status")
    List<Item> findItemByStatus(@Param("status") Boolean status);

    @Query("SELECT item FROM Item item WHERE item.categoria.id = :categoriaId")
    List<Item> findItemByCategoria(@Param("categoriaId") Long categoriaId);
}
