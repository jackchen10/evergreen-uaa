package org.evergreen.evergreenuaa.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.val;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Component
public class JwtUtil {
    //用于签名的key
    public static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public String createJwtToken(UserDetails userDetails) {
        val now = System.currentTimeMillis();

        return Jwts.builder()
                .setId("evergreen")
                .claim("authorites",userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(toList()))
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 60_000))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}
