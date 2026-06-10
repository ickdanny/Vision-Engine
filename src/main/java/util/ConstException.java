package util;

@SuppressWarnings("unused")
public class ConstException extends RuntimeException{
    public ConstException() {
    }

    public ConstException(String message) {
        super(message);
    }

    public ConstException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstException(Throwable cause) {
        super(cause);
    }

    public ConstException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}