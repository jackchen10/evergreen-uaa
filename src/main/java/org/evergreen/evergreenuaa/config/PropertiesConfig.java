package org.evergreen.evergreenuaa.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.Valid;

@Configuration
@ConfigurationProperties(prefix = "evergreen")
public class PropertiesConfig {

    @Getter
    @Setter
    private Jwt jwt = new Jwt();

    @Getter
    @Setter
    private AliParam aliParam = new AliParam();

    @Getter
    @Setter
    private SmsProvider smsProvider = new SmsProvider();

    @Getter
    @Setter
    public static class AliParam {
        private String apiKey;
        private String apiSecret;
    }

    @Getter
    @Setter
    public static class SmsProvider {
        private String name;
        private String apiUrl;
    }

    @Getter
    @Setter
    @Valid
    private LeanCloud leanCloud = new LeanCloud();

    @Getter
    @Setter
    @Valid
    private EmailProvider emailProvider = new EmailProvider();

    @Getter
    @Setter
    public static class LeanCloud {
        private String appId;
        private String appKey;
    }

    @Getter
    @Setter
    public static class EmailProvider {
        private String name;
        private String apiKey;
    }

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
