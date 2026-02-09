package com.jkomerce.store.mapper;

import com.jkomerce.store.dto.LoginDTO;
import com.jkomerce.store.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    UserDTO findUserByLoginId(String loginId);
    UserDTO selectUserById(Long id);
    int insertUser(UserDTO userDTO);
    int updateUser(UserDTO userDTO);
    int deleteUser(Long id);

}
