package org.evergreen.evergreenuaa.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.evergreen.evergreenuaa.config.PropertiesConfig;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.SignatureException;
import java.util.Date;
import java.util.Optional;
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

    /**
     * 用refreshToken去创建accessToken
     * @param token
     * @return 创建后的accessToken
     */
    public String createAccessTokenWithRefreshToken(String token) {
        return parseClaims(token, refreshKey)
                .map(claims -> Jwts.builder()
                        .setClaims(claims)
                        .setExpiration(new Date(System.currentTimeMillis() + propertiesConfig.getJwt().getAccessTokenExpireTime()))
                        .setIssuedAt(new Date())
                        .signWith(key, SignatureAlgorithm.HS512)
                        .compact()
                ).orElseThrow(() -> new AccessDeniedException("访问被拒绝"));
    }

    /**
     * 解析Claims
     * @param token
     * @param key
     * @return
     */
    private Optional<Claims> parseClaims(String token, Key key) {
        try {
            val claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return Optional.of(claims);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // validate access Token when token is not expired
    public boolean validateAccessTokenWithoutExpiration(String token) {
        return validateToken(token, key,false);
    }

    // validate access Token
    public boolean validateAccessToken(String token) {
        return validateToken(token, key,true);
    }

    //validate refresh Token
    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshKey,true);
    }

    public String createJwtToken(UserDetails userDetails, long timeToExpire, Key key) {
        val now = System.currentTimeMillis();

        return Jwts.builder()
                .setId("evergreen")
                .claim("authorities",userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(toList()))
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + timeToExpire))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateToken(String token, Key key, boolean isExpiredInvalid) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parse(token);
            return true;
        } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            if (e instanceof ExpiredJwtException)
            return !isExpiredInvalid;
        }
        return false;
    }
}
