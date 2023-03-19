package kr.yeonkyung.blog.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.yeonkyung.blog.Blog;
import kr.yeonkyung.blog.provider.exception.ApiClientErrorException;
import kr.yeonkyung.blog.provider.exception.ApiServerErrorException;
import kr.yeonkyung.common.exception.InternalServerErrorException;
import kr.yeonkyung.common.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@RequiredArgsConstructor
public abstract class ApiService<T> {
// API 호출 과정을 Facade Pattern을 사용해서 작성
// 호출 과정에서 API 서버별로 다른 부분은 abstract method로 추출해서 각 서버별로 구현(KakaoApiService, NaverApiService)하도록 작성

    protected final RestTemplate restTemplate;
    protected final ObjectMapper objectMapper;

    protected abstract HttpHeaders getHttpHeaders();

    protected abstract URI getUri(String query, String sort, Integer page, Integer size);

    protected abstract ResponseEntity<T> getResponseEntity(URI uri, HttpHeaders headers) throws RestClientException;

    protected abstract PagedResponse<Blog> parse(ResponseEntity<T> response, Integer page, Integer size);

    protected abstract String getKey();

    public PagedResponse<Blog> searchBlog(final String query, String sort, Integer page, Integer size) throws ApiClientErrorException, ApiServerErrorException {

        try {

            HttpHeaders headers = getHttpHeaders();
            URI uri = getUri(query, sort, page, size);
            ResponseEntity<T> response = getResponseEntity(uri, headers);

            return parse(response, page, size);

        } catch (RestClientException e) {

            // 클라이언트 에러인 경우 오류 내용을 사용자에게 보여주고, API 서버 에러인 경우 다른 API 서버를 사용하도록 구현
            
            // DefaultResponseErrorHandler에서 ClientHttpResponse의 상태 코드를 체크 
            // -> 상태 코드에 따라 CLIENT_ERROR(4xx), SERVER_ERROR(5xx)로 구분해서 예외를 던지는 것을 이용해 예외 처리

            // CLIENT_ERROR로 HttpClientErrorException을 던지는 경우, 사용자에게 전달할 에러 메시지 파싱
            if (e instanceof HttpClientErrorException) {

                HttpClientErrorException ex = (HttpClientErrorException) e;
                HttpStatus httpStatus = ex.getStatusCode();
                String bodyAsString = ex.getResponseBodyAsString();

                try {
                    Map<String, String> jsonMap = objectMapper.readValue(bodyAsString, Map.class);
                    String message = jsonMap.get(getKey());
                    throw new ApiClientErrorException(httpStatus, message, e);
                } catch (JsonProcessingException je) {
                    throw new InternalServerErrorException(je);
                }

            // SERVER_ERROR로 HttpServerErrorException을 던지는 경우, ApiServerErrorException로 변환해서 전달
            } else {
                throw new ApiServerErrorException(e);
            }
            
        // 그 외 예외는 InternalServerErrorException으로 변환 후 전달
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
    }
}
