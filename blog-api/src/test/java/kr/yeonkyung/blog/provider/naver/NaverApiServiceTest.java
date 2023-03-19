package kr.yeonkyung.blog.provider.naver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.yeonkyung.blog.Blog;
import kr.yeonkyung.blog.provider.exception.ApiClientErrorException;
import kr.yeonkyung.blog.provider.exception.ApiServerErrorException;
import kr.yeonkyung.common.response.PagedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class NaverApiServiceTest {

    @InjectMocks
    NaverApiService naverApiService;
    @Mock
    RestTemplate restTemplate;
    @Mock
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(naverApiService, "url", "http://localhost:8080");
        ReflectionTestUtils.setField(naverApiService, "clientId", "clientId");
        ReflectionTestUtils.setField(naverApiService, "clientSecret", "clientSecret");
    }

    @Test
    void 블로그_검색_상태값_200() {
        // given
        NaverResponse.Item item = new NaverResponse.Item();
        ReflectionTestUtils.setField(item, "title", "title");
        ReflectionTestUtils.setField(item, "link", "link");
        ReflectionTestUtils.setField(item, "description", "description");
        ReflectionTestUtils.setField(item, "bloggerName", "blogName");
        ReflectionTestUtils.setField(item, "postdate", "20230131");

        NaverResponse naverResponse = new NaverResponse();
        ReflectionTestUtils.setField(naverResponse, "lastBuildDate", "Mon, 20 Mar 2023 11:07:31 +0900");
        ReflectionTestUtils.setField(naverResponse, "total", 1563201);
        ReflectionTestUtils.setField(naverResponse, "start", 1);
        ReflectionTestUtils.setField(naverResponse, "display", 1);
        ReflectionTestUtils.setField(naverResponse, "items", List.of(item));

        ResponseEntity<NaverResponse> responseEntity = ResponseEntity.ok(naverResponse);
        doReturn(responseEntity)
                .when(restTemplate).exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.any(HttpEntity.class),
                        Mockito.any(ParameterizedTypeReference.class));
        // when
        PagedResponse<Blog> response = naverApiService.searchBlog("test", "accuracy", 1, 1);
        // then
        assertEquals(1563201, ReflectionTestUtils.getField(response.getMeta(), "total"));
        assertEquals(1, ReflectionTestUtils.getField(response.getMeta(), "page"));
        assertEquals(1, ReflectionTestUtils.getField(response.getMeta(), "size"));
    }

    @Test
    void 블로그_검색_클라이언트_에러() throws JsonProcessingException {
        // given
        HttpClientErrorException ex =
                HttpClientErrorException.create(
                        "400 Bad Request: [{'errorType': 'InvalidArgument', 'message': 'page is less than min'}]",
                        HttpStatus.BAD_REQUEST,
                        HttpStatus.BAD_REQUEST.name(),
                        new HttpHeaders(),
                        "{\"errorType\": \"InvalidArgument\", \"message\": \"page is less than min\"}".getBytes(StandardCharsets.UTF_8),
                        Charset.defaultCharset());
        doThrow(ex)
                .when(restTemplate).exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.any(HttpEntity.class),
                        Mockito.any(ParameterizedTypeReference.class));

        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("errorMessage", "page is less than min");
        doReturn(jsonMap)
                .when(objectMapper).readValue(ex.getResponseBodyAsString(), Map.class);
        // when
        // then
        ApiClientErrorException result = assertThrows(ApiClientErrorException.class, () -> {
            naverApiService.searchBlog("test", "accuracy", 1, 10);
        });
        assertEquals(HttpStatus.BAD_REQUEST, result.getHttpStatus());
        assertEquals("page is less than min", result.getMessage());
    }

    @Test
    void 블로그_검색_서버_에러() {
        // given
        doThrow(HttpServerErrorException.class)
                .when(restTemplate).exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.any(HttpEntity.class),
                        Mockito.any(ParameterizedTypeReference.class));
        // when
        // then
        assertThrows(ApiServerErrorException.class, () -> {
            naverApiService.searchBlog("test", "accuracy", 1, 10);
        });
    }
}
