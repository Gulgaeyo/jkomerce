package com.jkomerce.store.exception;

import com.jkomerce.store.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //400: 잘못된 요청 (파라미터/존재하지 않는 리소스등)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handlerBadRequest(IllegalArgumentException e, HttpServletRequest req ){
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", e.getMessage(), req);
    }

    //409: 상태 충돌 (이미 PAID인데 approve, PENDING 아닌 주문 결제 시도 등)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleConflict(IllegalStateException e, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "CONFLICT", e.getMessage(), req);
    }

    //500: 나머지 전부 (진짜 서버 오류)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternal(Exception e, HttpServletRequest req) {
        // 로그 찍고, 응답 메세지는 단순화
        e.printStackTrace();
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 오류가 발생하였습니다.", req);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message, HttpServletRequest req ){
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .code(code)
                .message(message)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
