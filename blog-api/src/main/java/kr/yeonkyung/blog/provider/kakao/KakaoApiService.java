package kr.yeonkyung.blog.provider.kakao;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.yeonkyung.blog.Blog;
import kr.yeonkyung.blog.provider.ApiService;
import kr.yeonkyung.common.response.PagedResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Order(value = Ordered.HIGHEST_PRECEDENCE)  // (네이버보다) 카카오 API 서비스 이용 우선순위를 더 높게 설정
@Component
public class KakaoApiService extends ApiService<KakaoResponse> {

    @Value("${api.provider.kakao.url}")
    private String url;
    @Value("${api.provider.kakao.token}")
    private String token;

    public KakaoApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        super(restTemplate, objectMapper);
    }

    @Override
    protected HttpHeaders getHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(AUTHORIZATION, token);
        return httpHeaders;
    }

    @Override
    protected URI getUri(String query, String sort, Integer page, Integer size) {
        return UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", query)
                .queryParam("sort", sort)
                .queryParam("page", page)
                .queryParam("size", size)
                .build().encode().toUri();
    }

    @Override
    protected ResponseEntity<KakaoResponse> getResponseEntity(URI uri, HttpHeaders headers) throws RestClientException {
        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<>(){});
    }

    @Override
    protected PagedResponse<Blog> parse(ResponseEntity<KakaoResponse> responseEntity, Integer page, Integer size) {
        KakaoResponse body = responseEntity.getBody();
        if (body != null) {
            List<Blog> blogList = body.getDocuments().stream()
                    .map(KakaoBlog::new).collect(Collectors.toList());
            return new PagedResponse<>(body.getMeta().getPageableCount(), page, size, blogList);
        }
        return PagedResponse.noItems();
    }

    @Override
    protected String getKey() {
        return "message";
    }
}
