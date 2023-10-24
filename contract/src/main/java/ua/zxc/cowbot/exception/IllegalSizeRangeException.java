package ua.zxc.cowbot.exception;

public class IllegalSizeRangeException extends RuntimeException {

    public IllegalSizeRangeException() {
    }

    public IllegalSizeRangeException(String message) {
        super(message);
    }

    public IllegalSizeRangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
