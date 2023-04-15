package org.evergreen.evergreenuaa.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.evergreen.evergreenuaa.config.PropertiesConfig;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Component
public class JwtUtil {
    //用于签名的访问令牌的密钥
    public static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    //用于签名的刷新令牌的密钥
    public static final Key refreshKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private final PropertiesConfig propertiesConfig;

    //创建访问Token
    public String createAccessToken(UserDetails userDetails) {
        return createJwtToken(userDetails,propertiesConfig.getJwt().getAccessTokenExpireTime(),key);
    }

    //创建刷新Token
    public String createRefreshToken(UserDetails userDetails) {
        return createJwtToken(userDetails,propertiesConfig.getJwt().getRefreshTokenExpireTime(), refreshKey);
    }

    public String createJwtToken(UserDetails userDetails, long timeToExpire, Key key) {
        val now = System.currentTimeMillis();

        return Jwts.builder()
                .setId("evergreen")
                .claim("authorites",userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(toList()))
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + timeToExpire))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}
