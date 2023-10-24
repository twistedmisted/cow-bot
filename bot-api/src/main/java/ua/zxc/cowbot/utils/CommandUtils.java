package ua.zxc.cowbot.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CommandUtils {

    private static BeanFactory beanFactory;

    @Autowired
    private CommandUtils(BeanFactory beanFactory) {
        CommandUtils.beanFactory = beanFactory;
    }

    public static String getValueForText(String text) {
        String regex = "([а-яА-ЯіїєґІЇЄҐa-zA-Z-+ ]+)";
        Pattern pattern = Pattern.compile(regex);
        try {
            Matcher matcher = pattern.matcher(text);
            if (!matcher.find()) {
                throw new IllegalStateException("Can not to find a match");
            }
            String group = matcher.group().strip();
            return (String) beanFactory.getBean("respectText", BinaryTree.class).get(group.toLowerCase());
        } catch (IllegalStateException e) {
            log.warn("Can not to get group from text: {}", text, e);
            return null;
        }
    }

    public static String getValueForSticker(String text) {
        return (String) beanFactory.getBean("respectSticker", BinaryTree.class)
                .get(text.codePoints()
                        .mapToObj(Integer::toHexString)
                        .collect(Collectors.joining(" "))
                        .toLowerCase()
                );
    }
}
