package com.back.demo.repository;

import com.back.demo.model.ItemMedia;
import com.back.demo.model.ItemMediaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemMediaRepository extends JpaRepository<ItemMedia, ItemMediaId> {

    List<ItemMedia> findByIdItem(Long idItem);
}
