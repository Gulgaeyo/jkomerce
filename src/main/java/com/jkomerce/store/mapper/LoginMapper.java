package com.jkomerce.store.mapper;

import com.jkomerce.store.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginMapper {

    UserDTO loginById(String loginId);
    int createUser(UserDTO userDTO);
}
