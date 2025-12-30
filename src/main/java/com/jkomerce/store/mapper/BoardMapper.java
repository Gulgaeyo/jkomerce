package com.jkomerce.store.mapper;

import com.jkomerce.store.dto.BoardDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {

    List<BoardDTO> selectAllBoards();
    BoardDTO selectBoardByName(String name);
    BoardDTO selectBoardById(int id);
    int insertBoard(BoardDTO board);
    int updateBoard(BoardDTO board);
    int deleteBoard(int id);
    List<BoardDTO> findBoardsByTitle(String title);
}
