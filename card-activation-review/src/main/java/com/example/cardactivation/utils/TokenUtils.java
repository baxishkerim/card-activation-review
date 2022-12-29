package com.example.cardactivation.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class TokenUtils {



        public static String getUsernameFromToken(String bearToken,String secret) {

        String token = "";

        if (bearToken != null && bearToken.startsWith("Bearer")) {
            token = bearToken.substring(7);
        }
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);

            return claims.getBody().getSubject();
        }

}
