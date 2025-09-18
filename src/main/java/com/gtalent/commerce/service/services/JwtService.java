package com.gtalent.commerce.service.services;


import com.gtalent.commerce.service.models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;


import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    //1.產生密鑰
    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode("dGlueXNhbWVoYW5kc29tZXlvdW5nY2FsbHJlY29yZGdpZnRpbnZlbnRlZHdpdGhvdXQ=");
        return Keys.hmacShaKeyFor(keyBytes);
    }


    //2.從JWT中 解析 出Email
    public String getEmailFromToken(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody().getSubject();

    }

    //3.負責 生產 (Jason Web token)
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 28800000))  //八小時後過期
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
