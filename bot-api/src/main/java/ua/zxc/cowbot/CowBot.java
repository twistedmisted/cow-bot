package ua.zxc.cowbot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.handler.MainHandler;

@Slf4j
public class CowBot extends TelegramWebhookBot {

    private String botPath;

    private String botUsername;

    private String botToken;

    private final MainHandler mainHandler;

    public CowBot(MainHandler mainHandler) {
        this.mainHandler = mainHandler;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotPath() {
        return botPath;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return mainHandler.createMessage(update);
    }

    public void setWebHookPath(String webHookPath) {
        this.botPath = webHookPath;
    }

    public void setBotUserName(String botUserName) {
        this.botUsername = botUserName;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }
}
