package com.jkomerce.store.auth;

import com.jkomerce.store.dto.UserDTO;
import com.jkomerce.store.exception.UnauthorizedException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class SessionUserProvider {

    public Long getRequiredUserId(HttpSession session) {
        if(session == null) throw new UnauthorizedException("로그인이 필요합니다.");

        Object v = session.getAttribute("userId");
        if(v == null) throw new UnauthorizedException("로그인이 필요합니다.");
        if(v instanceof Long) return (Long) v;
        if(v instanceof Integer) return ((Integer) v).longValue();

        Object userObj = session.getAttribute("user");
        if (userObj instanceof UserDTO) {
            Long id = ((UserDTO) userObj).getId();
            return id;
        }

        throw new UnauthorizedException("로그인이 필요합니다.");
    }
}
