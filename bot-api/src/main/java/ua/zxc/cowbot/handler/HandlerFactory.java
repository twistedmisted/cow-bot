package ua.zxc.cowbot.handler;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Component;
import ua.zxc.cowbot.exception.NotFoundCommandException;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;

import java.util.Set;

@Component
public class HandlerFactory {

    private String rawCommand;

    private final BeanFactory beanFactory;

    public HandlerFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public HandlerStrategy getMessageHandler() {
        try {
            return beanFactory.getBean(getMessageStrategyHandler(), HandlerStrategy.class);
        } catch (NotFoundCommandException e) {
            return beanFactory.getBean("WORD_HANDLER", HandlerStrategy.class);
        }
    }

    private String getMessageStrategyHandler() {
        return getStrategyNameByCommand(((Set<Class<?>>) beanFactory.getBean("commandHandlers")), getCommand());
    }

    private String getStrategyNameByCommand(Set<Class<?>> allMessageHandlers, String command) {
        for (Class<?> messageHandler : allMessageHandlers) {
            Handler annotation = messageHandler.getAnnotation(Handler.class);
            String[] commands = annotation.commands();
            for (String tempCommand : commands) {
                if (areCommandsEquals(command, tempCommand)) {
                    return annotation.value();
                }
            }
        }
        throw new NotFoundCommandException("Not found name of bean for " + command);
    }

    private boolean areCommandsEquals(String command, String tempCommand) {
        return command.equals(tempCommand);
    }

    private String getCommand() {
        if (rawCommand.contains("@")) {
            return rawCommand.substring(0, rawCommand.indexOf("@"));
        }
        if (rawCommand.contains(" ")) {
            return rawCommand.substring(0, rawCommand.indexOf(" "));
        }
        return rawCommand;
    }

    public HandlerStrategy getCallbackHandler() {
        try {
            return beanFactory.getBean(getCallbackStrategyName(), HandlerStrategy.class);
        } catch (NotFoundCommandException e) {
            // TODO: why return OTHER if it is not word handler
            return beanFactory.getBean("WORD_HANDLER", HandlerStrategy.class);
        }
    }

    private String getCallbackStrategyName() {
        return getStrategyNameByCallbackData(((Set<Class<?>>) beanFactory.getBean("callbackHandlers")), rawCommand);
    }

    private String getStrategyNameByCallbackData(Set<Class<?>> allMessageHandlers, String command) {
        for (Class<?> messageHandler : allMessageHandlers) {
            Handler annotation = messageHandler.getAnnotation(Handler.class);
            String[] commands = annotation.commands();
            for (String tempCommand : commands) {
                if (areCallbackDataEquals(command, tempCommand)) {
                    return annotation.value();
                }
            }
        }
        throw new NotFoundCommandException("Not found name of bean for " + command);
    }

    private boolean areCallbackDataEquals(String firstData, String secondData) {
        return firstData.contains(secondData);
    }

    public void setRawCommand(String rawCommand) {
        this.rawCommand = rawCommand;
    }
}
