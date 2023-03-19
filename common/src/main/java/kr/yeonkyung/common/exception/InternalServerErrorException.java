package kr.yeonkyung.common.exception;

public class InternalServerErrorException extends RuntimeException {

    public InternalServerErrorException() {
        super("INTERNAL_SERVER_ERROR");
    }

    public InternalServerErrorException(String message) {
        super(message);
    }

    public InternalServerErrorException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
