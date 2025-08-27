package com.nt.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtils {
    private static String signKey = "NTNTNTNTNTNTNTNTNTNTNTNTNTNTNTNT";
    private static final long SHORT_EXPIRE = 43200000L;   // 12 小时
    private static final long LONG_EXPIRE = 1209600000L;  // 14 天


    /**
     * 生成jwt
     *
     * @param claims
     * @return
     */
    public static String generateJwt(Map<String, Object> claims, Boolean rememberMe) {
        String jwt = Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signKey.getBytes(StandardCharsets.UTF_8))
                .setExpiration(new Date(System.currentTimeMillis() + (rememberMe ? LONG_EXPIRE : SHORT_EXPIRE)))
                .compact();
        return jwt;
    }

    /**
     * 解析jwt
     *
     **/
    public static Claims parseJwt(String jwt) throws JwtException {
        Claims claims = Jwts.parser()
                .setSigningKey(signKey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(jwt)
                .getBody();
        return claims;
    }
}



