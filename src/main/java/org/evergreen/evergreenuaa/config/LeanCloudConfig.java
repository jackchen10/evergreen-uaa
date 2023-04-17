package org.evergreen.evergreenuaa.config;

import cn.leancloud.AVLogger;
import cn.leancloud.core.AVOSCloud;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Configuration
public class LeanCloudConfig {

    private final PropertiesConfig propertiesConfig;
    private final Environment env;

    @PostConstruct()
    public void initialize() {
        if (env.acceptsProfiles(Profiles.of("prod"))) {
            AVOSCloud.setLogLevel(AVLogger.Level.ERROR);
        } else {
            AVOSCloud.setLogLevel(AVLogger.Level.DEBUG);
        }
        AVOSCloud.initialize(propertiesConfig.getLeanCloud().getAppId(), propertiesConfig.getLeanCloud().getAppKey());
    }
}
