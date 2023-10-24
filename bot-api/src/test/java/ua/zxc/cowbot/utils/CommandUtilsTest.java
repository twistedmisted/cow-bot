package ua.zxc.cowbot.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class CommandUtilsTest {

    @Test
    void existsRespectsWithOneLatinLowerCaseLetterAndWithSymbolShouldReturnTrue() {
        String value = CommandUtils.getValueForText("Дякую :)");
        assertEquals("respect", value);
    }

    @Test
    void existsRespectsWithOneLatinLowerCaseLetterWithoutSymbolShouldReturnTrue() {
        String value = CommandUtils.getValueForText("f");
        assertEquals("respect", value);
    }

    @Test
    void existsRespectsWithOneLatinUpperCaseLetterAndWithSymbolShouldReturnTrue() {
        String value = CommandUtils.getValueForText("F.");
        assertEquals("respect", value);
    }

    @Test
    void existsRespectsWithOneLatinUpperCaseLetterWithoutSymbolShouldReturnTrue() {
        String value = CommandUtils.getValueForText("F");
        assertEquals("respect", value);
    }

    @Test
    void existsRespectsWithOneLatinLowerCaseLetterAndWithSymbolShouldReturnFalse() {
        String value = CommandUtils.getValueForText("t.");
        assertNull(value);
    }

    @Test
    void existsRespectsWithOneLatinLowerCaseLetterWithoutSymbolShouldReturnFalse() {
        String value = CommandUtils.getValueForText("t");
        assertNull(value);
    }

    @Test
    void existsRespectsWithOneLatinUpperCaseLetterAndWithSymbolShouldReturnFalse() {
        String value = CommandUtils.getValueForText("T.");
        assertNull(value);
    }

    @Test
    void existsRespectsWithOneLatinUpperCaseLetterWithoutSymbolShouldReturnFalse() {
        String value = CommandUtils.getValueForText("T");
        assertNull(value);
    }

    @Test
    void existsRespectsWithOneLatinLowerCaseWordAndWithSymbolShouldReturnTrue() {
        String value = CommandUtils.getValueForText("thanks.");
        assertEquals("respect", value);
    }

    @Test
    void existsRespectsWithOneLatinLowerCaseWordWithoutSymbolShouldReturnTrue() {
        String value = CommandUtils.getValueForText("thanks");
        assertEquals("respect", value);
    }

    @Test
    void existsRespectsWithOneLatinUpperCaseWordAndWithSymbolShouldReturnTrue() {
        String value = CommandUtils.getValueForText("THANKS.");
        assertEquals("respect", value);
    }

    @Test
    void existsRespectsWithOneLatinUpperCaseWordWithoutSymbolShouldReturnTrue() {
        String value = CommandUtils.getValueForText("THANKS");
        assertEquals("respect", value);
    }

    @Test
    void existsRespectsWithOneLatinLowerCaseWordAndWithSymbolShouldReturnFalse() {
        String value = CommandUtils.getValueForText("word.");
        assertNull(value);
    }

    @Test
    void existsRespectsWithOneLatinLowerCaseWordWithoutSymbolShouldReturnFalse() {
        String value = CommandUtils.getValueForText("word");
        assertNull(value);
    }

    @Test
    void existsRespectsWithOneLatinUpperCaseWordAndWithSymbolShouldReturnFalse() {
        String value = CommandUtils.getValueForText("WORD.");
        assertNull(value);
    }

    @Test
    void existsRespectsWithOneLatinUpperCaseWordWithoutSymbolShouldReturnFalse() {
        String value = CommandUtils.getValueForText("WORD");
        assertNull(value);
    }

    @Test
    void existsRespectsWithTwoLatinLowerCaseWordAndWithSymbolShouldReturnTrue() {
        String value = CommandUtils.getValueForText("thank you.");
        assertEquals("respect", value);
    }

    @Test
    void existsRespectsWithTwoLatinLowerCaseWordWithoutSymbolShouldReturnTrue() {
        String value = CommandUtils.getValueForText("thank you");
        assertEquals("respect", value);
    }

    @Test
    void existsRespectsWithTwoLatinUpperCaseWordAndWithSymbolShouldReturnTrue() {
        String value = CommandUtils.getValueForText("THANK YOU.");
        assertEquals("respect", value);
    }

    @Test
    void existsRespectsWithTwiLatinUpperCaseWordWithoutSymbolShouldReturnTrue() {
        String value = CommandUtils.getValueForText("THANK YOU");
        assertEquals("respect", value);
    }

    @Test
    void existsRespectsWithTwoLatinLowerCaseWordAndWithSymbolShouldReturnFalse() {
        String value = CommandUtils.getValueForText("wrong word.");
        assertNull(value);
    }

    @Test
    void existsRespectsWithTwoLatinLowerCaseWordWithoutSymbolShouldReturnFalse() {
        String value = CommandUtils.getValueForText("wrong word");
        assertNull(value);
    }

    @Test
    void existsRespectsWithTwoLatinUpperCaseWordAndWithSymbolShouldReturnFalse() {
        String value = CommandUtils.getValueForText("WRONG WORD.");
        assertNull(value);
    }

    @Test
    void existsRespectsWithTwoLatinUpperCaseWordWithoutSymbolShouldReturnFalse() {
        String value = CommandUtils.getValueForText("WRONG WORD");
        assertNull(value);
    }

    @Test
    void existsRespectsWithFourCyrillicLowerCaseWordWithoutSymbolShouldReturnTrue() {
        String value = CommandUtils.getValueForText("хуй тобі в сраку");
        assertEquals("disrespect", value);
    }

    @Test
    void existsRespectsWithFourCyrillicLowerCaseWordWithSymbolShouldReturnTrue() {
        String value = CommandUtils.getValueForText("хуй тобі в сраку)");
        assertEquals("disrespect", value);
    }

    @Test
    void existsRespectsWithFourCyrillicUpperCaseWordWithoutSymbolShouldReturnTrue() {
        String value = CommandUtils.getValueForText("ХУЙ ТОБІ В СРАКУ");
        assertEquals("disrespect", value);
    }

    @Test
    void existsRespectsWithFourCyrillicUpperCaseWordWithSymbolShouldReturnTrue() {
        String value = CommandUtils.getValueForText("ХУЙ ТОБІ В СРАКУ)");
        assertEquals("disrespect", value);
    }

    @Test
    void existsRespectsWithMoreThanFourCyrillicLowerCaseWordsWithoutSymbolShouldReturnFalse() {
        String value = CommandUtils.getValueForText("хуй тебе в жопу лох");
        assertNull(value);
    }

    @Test
    void existsRespectsWithMoreThanFourCyrillicUpperCaseWordsWithoutSymbolShouldReturnFalse() {
        String value = CommandUtils.getValueForText("ХУЙ ТЕБЕ В ЖОПУ ЛОХ");
        assertNull(value);
    }

}