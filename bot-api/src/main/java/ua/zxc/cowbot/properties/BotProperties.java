package ua.zxc.cowbot.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "telegram")
@Setter
@Getter
public class BotProperties {

    private String webHookPath;
    private String userName;
    private String botToken;
}
