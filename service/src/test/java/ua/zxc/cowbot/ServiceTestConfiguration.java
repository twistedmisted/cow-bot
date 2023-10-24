package ua.zxc.cowbot;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ua.zxc.cowbot.config.ServiceConfiguration;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableConfigurationProperties
public class ServiceTestConfiguration extends ServiceConfiguration {
}
