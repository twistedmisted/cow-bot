package ua.zxc.cowbot.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParseData {

    private static String getSubstringAfterFirstUnderscore(String data) {
        return data.substring(0, data.indexOf('_'));
    }

    public static Integer parseIntAfterLastUnderscore(String data) {
        log.info("Parsing last int value from data: {}", data);
        return Integer.parseInt(getSubstringAfterLastUnderscore(data));
    }

    private static String getSubstringAfterLastUnderscore(String data) {
        return data.substring(data.lastIndexOf('_') + 1);
    }

    public static Long parseLongAfterFirstUnderscore(String data) {
        log.info("Parsing first long value from data: {}", data);
        return Long.parseLong(getSubstringAfterFirstUnderscore(data));
    }

    public static Long parseLongAfterLastUnderscore(String data) {
        log.info("Parsing fast long value from data: {}", data);
        return Long.parseLong(getSubstringAfterLastUnderscore(data));
    }
}
