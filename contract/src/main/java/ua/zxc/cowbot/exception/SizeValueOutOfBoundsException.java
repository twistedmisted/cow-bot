package ua.zxc.cowbot.exception;

public class SizeValueOutOfBoundsException extends RuntimeException {

    public SizeValueOutOfBoundsException() {
    }

    public SizeValueOutOfBoundsException(String message) {
        super(message);
    }

    public SizeValueOutOfBoundsException(String message, Throwable cause) {
        super(message, cause);
    }
}
