package ua.zxc.cowbot.utils;

import com.vdurmont.emoji.EmojiParser;

public enum Emoji {
    WHITE_CHECK_MARK(EmojiParser.parseToUnicode(":white_check_mark:")),
    ARROW_RIGHT(EmojiParser.parseToUnicode(":arrow_right:")),
    ARROW_LEFT(EmojiParser.parseToUnicode(":arrow_left:")),
    REPEAT(EmojiParser.parseToUnicode(":repeat:")),
    GEAR(EmojiParser.parseToUnicode(":gear:")),
    PARTY(EmojiParser.parseToUnicode(":partying_face:")),
    HEART(EmojiParser.parseToUnicode(":heart:")),
    JOY(EmojiParser.parseToUnicode(":joy:"));

    final String name;

    Emoji(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
