package com.back.demo.repository;

import com.back.demo.model.ItemMedia;
import com.back.demo.model.ItemMediaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemMediaRepository extends JpaRepository<ItemMedia, ItemMediaId> {

    List<ItemMedia> findByIdItem(Long idItem);

    @Query("SELECT media FROM ItemMedia media WHERE media.id.item_id = :item_id AND media.id.seq = :sequencia")
    ItemMedia findMediaById(@Param("item_id") Long itemId, @Param("sequencia") Integer sequencia);

    @Query("SELECT MAX(media.id.seq) FROM ItemMedia media WHERE media.id.item_id = :item_id")
    Integer findMaxSeqByItemId(@Param("item_id") Long itemId);
}
