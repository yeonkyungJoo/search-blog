package kr.yeonkyung.blog.provider.naver;

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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Order(value = Ordered.LOWEST_PRECEDENCE)
@Component
public class NaverApiService extends ApiService<NaverResponse> {

    private final String NAVER_CLIENT_ID = "X-Naver-Client-Id";
    private final String NAVER_CLIENT_SECRET = "X-Naver-Client-Secret";
    @Value("${api.provider.naver.url}")
    private String url;
    @Value("${api.provider.naver.client-id}")
    private String clientId;
    @Value("${api.provider.naver.client-secret}")
    private String clientSecret;

    public NaverApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        super(restTemplate, objectMapper);
    }

    @Override
    protected HttpHeaders getHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(NAVER_CLIENT_ID, clientId);
        httpHeaders.set(NAVER_CLIENT_SECRET, clientSecret);
        return httpHeaders;
    }

    @Override
    protected URI getUri(String query, String sort, Integer page, Integer size) {
        return UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", query)
                .queryParam("sort", sort.equals("accuracy") ? "sim" : "date")
                .queryParam("start", (page - 1) * size + 1)
                .queryParam("display", size)
                .build().encode().toUri();
    }

    @Override
    protected ResponseEntity<NaverResponse> getResponseEntity(URI uri, HttpHeaders headers) throws RestClientException {
        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<>(){});
    }

    @Override
    protected PagedResponse<Blog> parse(ResponseEntity<NaverResponse> responseEntity, Integer page, Integer size) {
        NaverResponse body = responseEntity.getBody();
        if (body != null) {
            List<Blog> blogList = body.getItems().stream().map(NaverBlog::new).collect(Collectors.toList());
            return new PagedResponse<>(body.getTotal(), page, size, blogList);
        }
        return PagedResponse.noItems();
    }

    @Override
    protected String getKey() {
        return "errorMessage";
    }
}
