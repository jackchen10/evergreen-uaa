package org.evergreen.evergreenuaa.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "evergreen")
public class PropertiesConfig {

    @Getter
    @Setter
    private Jwt jwt = new Jwt();


    @Getter
    @Setter
    public static class Jwt {
        private String header = "Authorization";
        private String prefix = "Bearer ";
        //Access Token过期时间
        private Long accessTokenExpireTime = 60_000L;

        //Refresh Token过期时间
        private Long refreshTokenExpireTime = 30 * 24 * 3600 * 1000L;

    }
}
