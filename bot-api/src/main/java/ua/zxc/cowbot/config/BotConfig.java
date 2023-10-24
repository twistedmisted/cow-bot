package ua.zxc.cowbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.zxc.cowbot.CowBot;
import ua.zxc.cowbot.handler.MainHandler;
import ua.zxc.cowbot.properties.BotProperties;

@Configuration
public class BotConfig {

    private final BotProperties botProperties;

    public BotConfig(BotProperties botProperties) {
        this.botProperties = botProperties;
    }

    @Bean
    public CowBot createCowBot(MainHandler mainHandler) {
        CowBot mySuperTelegramBot = new CowBot(mainHandler);
        mySuperTelegramBot.setBotUserName(botProperties.getUserName());
        mySuperTelegramBot.setBotToken(botProperties.getBotToken());
        mySuperTelegramBot.setWebHookPath(botProperties.getWebHookPath());
        return mySuperTelegramBot;
    }
}
