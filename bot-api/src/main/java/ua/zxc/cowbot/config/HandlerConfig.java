package ua.zxc.cowbot.config;

import org.springframework.data.util.AnnotatedTypeScanner;
import ua.zxc.cowbot.handler.annotation.Handler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class HandlerConfig {

    private static final String COMMAND_HANDLERS_PACKAGE = "ua.zxc.cowbot.handler.types.command";
    private static final String CALLBACK_HANDLERS_PACKAGE = "ua.zxc.cowbot.handler.types.callback";

    private static final AnnotatedTypeScanner ANNOTATED_TYPE_SCANNER =
            new AnnotatedTypeScanner(false, Handler.class);

    @Bean("commandHandlers")
    public Set<Class<?>> commandHandlers() {
        return ANNOTATED_TYPE_SCANNER.findTypes(COMMAND_HANDLERS_PACKAGE);
    }

    @Bean("callbackHandlers")
    public Set<Class<?>> callbackHandlers() {
        return ANNOTATED_TYPE_SCANNER.findTypes(CALLBACK_HANDLERS_PACKAGE);
    }
}
