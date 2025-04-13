package com.phantom.userservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(String username,Integer userId){
        return Jwts.builder()
                .setSubject(username)
                .claim("userId",userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(SignatureAlgorithm.HS256,secret)
                .compact();
    }
    public Claims parseToken(String token){
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
    public String getUsernameFromToken(String token){
        Claims claims=parseToken((token));
        return claims.getSubject();
    }

    public Integer getUserIdFromToken(String token){
        Claims claims=parseToken(token);
        return claims.get("userId",Integer.class);
    }

    public String extractToken(HttpServletRequest request){
        String bearerToken=request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken)&&bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token){
        try{
            Claims claims=parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e){
            return false;
        }
    }

}
