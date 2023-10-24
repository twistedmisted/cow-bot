package ua.zxc.cowbot.exception;

public class MessageException extends RuntimeException {

    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
