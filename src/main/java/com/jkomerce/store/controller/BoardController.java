package com.jkomerce.store.controller;

import com.jkomerce.store.dto.BoardDTO;
import com.jkomerce.store.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;

    @GetMapping
    public List<BoardDTO> getBoardPage(@RequestParam(required = false) String title){
        if(title == null || title.isEmpty()){
            return boardService.getBoard();
        }
        return boardService.getBoardsByTitle(title);
    }

    @GetMapping("/{id}")
    public BoardDTO getDetailBoard(@PathVariable int id){
        return boardService.getBoardById(id);
    }

    @PostMapping
    public int addBoard(@RequestBody BoardDTO boardDTO) {
        return boardService.createBoard(boardDTO);
    }

    @PutMapping("/{id}")
    public int modifyBoard(@PathVariable Long id, @RequestBody BoardDTO boardDTO) {

        boardDTO.setId(id);
        return boardService.updateBoard(boardDTO);
    }

    @DeleteMapping("/{id}")
    public int deleteBoard(@PathVariable int id) {
        return boardService.deleteBoard(id);
    }
}
