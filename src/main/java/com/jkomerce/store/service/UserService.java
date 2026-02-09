package com.jkomerce.store.service;

import com.jkomerce.store.dto.LoginDTO;
import com.jkomerce.store.dto.UserDTO;
import com.jkomerce.store.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    // Mapper
    private final UserMapper userMapper;
    // Bcrypt
    private final PasswordEncoder passwordEncoder;

    // 트랜잭션으로 하나 실패 시 전부 무효 처리
    @Transactional
    public int signUp(UserDTO userDTO) {

        UserDTO exist = userMapper.findUserByLoginId(userDTO.getLoginId());
        if (exist != null) {
            throw new IllegalStateException("이미 사용 중인 아이디입니다.");
        }
        // 비밀번호 처리는 후에 진행 예정
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(encodedPassword);


        return userMapper.insertUser(userDTO);
    }

    public UserDTO login(LoginDTO loginDTO) {
        UserDTO user = userMapper.findUserByLoginId(loginDTO.getLoginId());
        // LoginId를 통한 아이디 존재 여부
        if (user == null) {
            return null;
        }
        // 비밀번호 확인
        if(!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())){
            return null;
        }
        // 응답에서 비밀번호 유출X
        user.setPassword(null);

        return user;
    }

    //내 정보 조회시 사용
    public UserDTO selectUserById(Long id) {
        return userMapper.selectUserById(id);
    }

    @Transactional
    public int updateUser(UserDTO userDTO) {
        return userMapper.updateUser(userDTO);
    }

    @Transactional
    public int deleteUser(Long id) {
        return userMapper.deleteUser(id);
    }

}
