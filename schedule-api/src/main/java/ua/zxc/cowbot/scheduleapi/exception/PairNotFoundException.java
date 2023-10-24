package ua.zxc.cowbot.scheduleapi.exception;

public class PairNotFoundException extends Exception {

    public PairNotFoundException() {
        super("Today is day of");
    }

    public PairNotFoundException(String message) {
        super(message);
    }

    public PairNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
