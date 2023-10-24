package ua.zxc.cowbot.handler.types.other;

import org.springframework.beans.factory.BeanFactory;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.TelegramService;
import ua.zxc.cowbot.utils.CommandUtils;

import static java.util.Objects.isNull;

@Handler(value = "WORD_HANDLER")
public class WordsHandler implements HandlerStrategy {

    private final TelegramService telegramService;

    private final BeanFactory beanFactory;

    public WordsHandler(TelegramService telegramService, BeanFactory beanFactory) {
        this.telegramService = telegramService;
        this.beanFactory = beanFactory;
    }

    @Override
    public SendMessage handleMessage(Update update) {
        SendMessage sendMessage;
        String text = update.getMessage().getText();

        // TODO: make it better
        String valueForText = CommandUtils.getValueForText(text);
        sendMessage = getSendMessage(update, valueForText);
        if (sendMessage != null) {
            return sendMessage;
        }
        String valueForSticker = CommandUtils.getValueForSticker(text);
        sendMessage = getSendMessage(update, valueForSticker);
        if (sendMessage != null) {
            return sendMessage;
        }

        switch (text) {
            case "анг":
            case "/english":
                ForwardMessage forwardMessage = new ForwardMessage();
                forwardMessage.setFromChatId(String.valueOf(473625679));
                forwardMessage.setChatId(String.valueOf(-1001238331811L));

                forwardMessage.setMessageId(2977);
                telegramService.forwardMessage(forwardMessage);

                forwardMessage.setMessageId(2978);
                telegramService.forwardMessage(forwardMessage);
                sendMessage = new SendMessage();
                break;
            default:
                sendMessage = new SendMessage();
                break;
        }
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        return sendMessage;
    }

    private SendMessage getSendMessage(Update update, String valueForText) {
        if (isNull(valueForText)) {
            return new SendMessage();
        }
        if (valueForText.equals("respect")) {
            return beanFactory.getBean("PAY_RESPECT", HandlerStrategy.class).handleMessage(update);
        } else {
            return beanFactory.getBean("PAY_DISRESPECT", HandlerStrategy.class).handleMessage(update);
        }
    }
}

