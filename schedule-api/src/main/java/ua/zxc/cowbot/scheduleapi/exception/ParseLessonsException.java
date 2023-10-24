package ua.zxc.cowbot.scheduleapi.exception;

public class ParseLessonsException extends Exception {

    public ParseLessonsException() {
        super("Cannot to parse lessons because the response is null");
    }

    public ParseLessonsException(String message) {
        super(message);
    }

    public ParseLessonsException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
