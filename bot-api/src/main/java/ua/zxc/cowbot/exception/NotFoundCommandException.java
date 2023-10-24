package ua.zxc.cowbot.exception;

public class NotFoundCommandException extends RuntimeException {

    public NotFoundCommandException(String message) {
        super(message);
    }
}
