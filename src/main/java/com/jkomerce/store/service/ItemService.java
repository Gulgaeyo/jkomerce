package com.jkomerce.store.service;

import com.jkomerce.store.dto.ItemDTO;
import com.jkomerce.store.mapper.ItemMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    private final ItemMapper itemMapper;

    public ItemService(ItemMapper itemMapper) {this.itemMapper = itemMapper;}

    public List<ItemDTO> getItems() {return itemMapper.selectAllItems();}

    public ItemDTO getItemById(Integer id) {return itemMapper.selectItemById(id);}

    public int createItem(ItemDTO itemDTO) {return itemMapper.insertItem(itemDTO);}
    public int updateItem(ItemDTO itemDTO) {return itemMapper.updateItem(itemDTO);}
    public int deleteItem(Integer id) {return itemMapper.deleteItem(id);}

    public List<ItemDTO> getItemsByName(String name) {return itemMapper.selectItemsByName(name);}
}
