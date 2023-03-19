package kr.yeonkyung.blog.advice;

import kr.yeonkyung.blog.provider.exception.ApiClientErrorException;
import kr.yeonkyung.blog.provider.exception.ApiServerErrorException;
import kr.yeonkyung.common.exception.InternalServerErrorException;
import kr.yeonkyung.common.response.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler(ApiClientErrorException.class)
    public ResponseEntity<Error> handleApiClientErrorException(ApiClientErrorException e) {
        log.error("ApiClientErrorException : {}", e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(new Error(e.getHttpStatus().name(), e.getMessage()));
    }

    @ExceptionHandler(ApiServerErrorException.class)
    public ResponseEntity<Error> handleApiServerErrorException(ApiServerErrorException e) {
        log.error("ApiServerErrorException : {}", e.getMessage(), e);
        return ResponseEntity.internalServerError()
                .body(new Error(HttpStatus.INTERNAL_SERVER_ERROR.name(), e.getMessage()));
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<Error> handleInternalServerErrorException(InternalServerErrorException e) {
        log.error("InternalServerErrorException : {}", e.getMessage(), e);
        return ResponseEntity.internalServerError()
                .body(new Error(HttpStatus.INTERNAL_SERVER_ERROR.name(), e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException : {}", e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(new Error(HttpStatus.BAD_REQUEST.name(), e.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Error> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException : {}", e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(new Error(HttpStatus.BAD_REQUEST.name(), e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Error> handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException : {}", e.getMessage(), e);
        return ResponseEntity.internalServerError()
                .body(new Error(HttpStatus.INTERNAL_SERVER_ERROR.name(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleException(Exception e) {
        log.error("Exception : {}", e.getMessage(), e);
        return ResponseEntity.internalServerError()
                .body(new Error(HttpStatus.INTERNAL_SERVER_ERROR.name(), e.getMessage()));
    }
}
