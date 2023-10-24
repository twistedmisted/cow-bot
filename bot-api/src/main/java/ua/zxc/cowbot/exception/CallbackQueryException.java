package ua.zxc.cowbot.exception;

public class CallbackQueryException extends RuntimeException {

    public CallbackQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
