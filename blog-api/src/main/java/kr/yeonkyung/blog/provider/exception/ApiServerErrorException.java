package kr.yeonkyung.blog.provider.exception;

public class ApiServerErrorException extends RuntimeException {
// API 서버에서 장애가 발생한 경우 등 (API 서버 에러)
// API 서버 이용 불가 시 전달하는 예외
    
    public ApiServerErrorException(Throwable cause) {
        super(cause);
    }
}
