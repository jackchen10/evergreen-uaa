package org.evergreen.evergreenuaa.config;

import com.sendgrid.SendGrid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class EmailConfig {

    private final PropertiesConfig propertiesConfig;

    @ConditionalOnProperty(prefix = "evergreen.email-provider", name = "api-key")
    @Bean
    public SendGrid sendGrid() {
        return new SendGrid(propertiesConfig.getEmailProvider().getApiKey());
    }
}
