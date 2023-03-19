package kr.yeonkyung.blog.provider.exception;

import org.springframework.http.HttpStatus;

public class ApiClientErrorException extends RuntimeException {
// API를 잘못 호출한 경우 전달하는 예외 (클라이언트 에러)
    
    private HttpStatus httpStatus;

    public ApiClientErrorException(HttpStatus httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
