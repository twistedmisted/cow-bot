package ua.zxc.cowbot.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ua.zxc.cowbot.scheduleapi.config.ScheduleApiConfiguration;

@Configuration
@Import({ServiceConfiguration.class, ScheduleApiConfiguration.class})
@ComponentScan(basePackages = {"ua.zxc.cowbot"})
public class BotApiConfiguration {
}
