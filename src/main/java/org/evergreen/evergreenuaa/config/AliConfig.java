package org.evergreen.evergreenuaa.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class AliConfig {

    private final PropertiesConfig propertiesConfig;

   @Bean
    public IAcsClient iAcsClient() {
        DefaultProfile profile = DefaultProfile.getProfile(
                "cn-hangzhou",
                propertiesConfig.getAliParam().getApiKey(),
                propertiesConfig.getAliParam().getApiSecret()
        );
        return new DefaultAcsClient(profile);
    }
}
