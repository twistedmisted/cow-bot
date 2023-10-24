package ua.zxc.cowbot.handler.types.other;

import org.springframework.beans.factory.BeanFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.utils.BinaryTree;

import java.util.stream.Collectors;

@Handler(value = "STICKER")
public class StickerHandler implements HandlerStrategy {

    private final BeanFactory beanFactory;

    public StickerHandler(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public SendMessage handleMessage(Update update) {
        String emoji = update.getMessage().getSticker().getEmoji();
        String value = getValueForStickerKey(emoji);
        if (value == null || value.isBlank()) {
            return new SendMessage();
        }
        if (value.equals("respect")) {
            return beanFactory.getBean("PAY_RESPECT", HandlerStrategy.class).handleMessage(update);
        }
        return beanFactory.getBean("PAY_DISRESPECT", HandlerStrategy.class).handleMessage(update);
    }

    private String getValueForStickerKey(String emoji) {
        return (String) beanFactory.getBean("respectSticker", BinaryTree.class)
                .get(emoji.codePoints()
                        .mapToObj(Integer::toHexString)
                        .collect(Collectors.joining(" "))
                        .toLowerCase()
                );
    }
}
