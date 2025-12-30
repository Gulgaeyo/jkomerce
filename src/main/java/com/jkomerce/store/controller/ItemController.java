package com.jkomerce.store.controller;

import com.jkomerce.store.dto.ItemDTO;
import com.jkomerce.store.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@Controller
@RequiredArgsConstructor
//@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/main")
    @ResponseBody
    public List<ItemDTO> getItemPage(@RequestParam(required = false) String name) {
        if(name==null || name.isEmpty()){
            return itemService.getItems();
        }
        return itemService.getItemsByName(name);
    }

    @GetMapping("/detail/{id}")
    @ResponseBody
    public ItemDTO getDetailPage(@PathVariable int id) {
        return itemService.getItemById(id);
    }

//    @GetMapping("/search/{name}")
//    @ResponseBody
//    public List<ItemDTO> searchItemByName(@PathVariable String name) {
//        return itemService.getItemsByName(name);
//    }


}
