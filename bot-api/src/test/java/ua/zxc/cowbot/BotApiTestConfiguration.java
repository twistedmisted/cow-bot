package ua.zxc.cowbot;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ua.zxc.cowbot.config.BotApiConfiguration;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableConfigurationProperties
public class BotApiTestConfiguration extends BotApiConfiguration {
}
