package com.alkemy.security.jwt;

import com.alkemy.security.dto.JwtDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtReturnReq {

    @Autowired
    private JwtProvider jwtProvider;

    public JwtDTO getTokenRequest(Authentication authentication) {
        String jwt = jwtProvider.generateToken(authentication);
        return new JwtDTO(jwt);
    }
}
