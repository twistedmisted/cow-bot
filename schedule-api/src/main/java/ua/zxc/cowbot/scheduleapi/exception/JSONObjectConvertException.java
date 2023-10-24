package ua.zxc.cowbot.scheduleapi.exception;

public class JSONObjectConvertException extends Exception {

    public JSONObjectConvertException() {
        super("String cannot be converted to JSONObject");
    }

    public JSONObjectConvertException(String message) {
        super(message);
    }

    public JSONObjectConvertException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
