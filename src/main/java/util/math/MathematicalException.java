package util.math;

public class MathematicalException extends RuntimeException{
    public MathematicalException() {
        super();
    }

    public MathematicalException(String message) {
        super(message);
    }

    public MathematicalException(String message, Throwable cause) {
        super(message, cause);
    }

    public MathematicalException(Throwable cause) {
        super(cause);
    }

    protected MathematicalException(String message, Throwable cause,
                                    boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
