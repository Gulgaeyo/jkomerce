package com.jkomerce.store.controller;

import com.jkomerce.store.dto.LoginDTO;
import com.jkomerce.store.dto.UserDTO;
import com.jkomerce.store.service.UserService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserDTO userDTO){
        int result = userService.signUp(userDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    //세션 담아서 실행
    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest request,
                                   @RequestBody LoginDTO loginDTO){
        UserDTO user = userService.login(loginDTO);
        if(user == null){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("userId", user.getId());


        return ResponseEntity.ok(user);
    }

    // LOGOUT 세션 종료 (나중에 구현)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){

        HttpSession session = request.getSession(false);
        if(session != null){
            session.invalidate();
        }

        return ResponseEntity.ok().build();
    }
}
