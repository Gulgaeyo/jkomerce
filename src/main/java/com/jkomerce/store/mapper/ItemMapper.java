package com.jkomerce.store.mapper;

import com.jkomerce.store.dto.ItemDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemMapper {
    List<ItemDTO> selectAllItems();
    ItemDTO selectItemById(Long id);
    int insertItem(ItemDTO item);
    int updateItem(ItemDTO item);
    int deleteItem(int id);
    List<ItemDTO> selectItemsByName(String name);
    int decreaseStock(@Param("itemId") Long itemId, @Param("quantity") Integer quantity);
}
