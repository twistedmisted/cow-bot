package ua.zxc.cowbot.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RestApiConfiguration.class})
@ComponentScan(basePackages = {"ua.zxc.cowbot"})
public class AppConfiguration {
}
