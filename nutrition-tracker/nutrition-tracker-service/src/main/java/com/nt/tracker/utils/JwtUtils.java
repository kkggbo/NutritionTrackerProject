package com.nt.tracker.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtils {
    private static String signKey = "NTNTNTNTNTNTNTNTNTNTNTNTNTNTNTNT";
    private static Long expire = 43200000L;

    /**
     * 生成jwt
     *
     * @param claims
     * @return
     */
    public static String generateJwt(Map<String, Object> claims) {
        String jwt = Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signKey.getBytes(StandardCharsets.UTF_8))
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .compact();
        return jwt;
    }

    /**
     * 解析jwt
     *
     **/
    public static Claims parseJwt(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(signKey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(jwt)
                .getBody();
        return claims;
    }
}



