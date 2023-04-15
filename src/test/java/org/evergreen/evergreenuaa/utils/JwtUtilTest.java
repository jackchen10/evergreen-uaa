package org.evergreen.evergreenuaa.utils;

import io.jsonwebtoken.Jwts;
import lombok.val;
import org.evergreen.evergreenuaa.config.PropertiesConfig;
import org.evergreen.evergreenuaa.entity.Role;
import org.evergreen.evergreenuaa.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    public void setup() {
        jwtUtil = new JwtUtil(new PropertiesConfig());
    }

    @Test
    public void createJwtTokenSuccessTest() {
        val username = "user";
        Set<Role> authoritiesSet = new HashSet<>();
        authoritiesSet.add(Role.builder().authority("ROLE_ADMIN").build());
        authoritiesSet.add(Role.builder().authority("ROLE_USER").build());
        val user = User.builder()
                .username(username)
                .authorities(authoritiesSet)
                .build();

        //生成JWT Token
        val token = jwtUtil.createAccessToken(user);

        //解析JWT
        val paseredClaims = Jwts.parserBuilder()
                .setSigningKey(JwtUtil.key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(username, paseredClaims.getSubject(), "解析完成后，Subject应该是用户名");
    }
}
