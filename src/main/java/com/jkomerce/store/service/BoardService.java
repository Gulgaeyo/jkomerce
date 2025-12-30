package com.jkomerce.store.service;

import com.jkomerce.store.dto.BoardDTO;
import com.jkomerce.store.mapper.BoardMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {
    private final BoardMapper boardMapper;

    public BoardService(BoardMapper boardMapper) {this.boardMapper = boardMapper;}
    public List<BoardDTO> getBoard() {return boardMapper.selectAllBoards();}
    public BoardDTO getBoardByName(String name) {return boardMapper.selectBoardByName(name);}
    public BoardDTO getBoardById(int id) {return boardMapper.selectBoardById(id);}

    public int createBoard(BoardDTO boardDTO) {return boardMapper.insertBoard(boardDTO);}
    public int updateBoard(BoardDTO boardDTO) {return boardMapper.updateBoard(boardDTO);}
    public int deleteBoard(int id) {return boardMapper.deleteBoard(id);}

    public List<BoardDTO> getBoardsByTitle(String title){return boardMapper.findBoardsByTitle(title);}


}
