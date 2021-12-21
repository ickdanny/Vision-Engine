package internalconfig;

public class InternalPropertyException extends RuntimeException{
    public InternalPropertyException() {
    }

    public InternalPropertyException(String message) {
        super(message);
    }

    public InternalPropertyException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalPropertyException(Throwable cause) {
        super(cause);
    }

    public InternalPropertyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
