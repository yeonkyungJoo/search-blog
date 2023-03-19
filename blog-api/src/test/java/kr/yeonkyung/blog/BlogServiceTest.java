package kr.yeonkyung.blog;

import kr.yeonkyung.blog.provider.ApiService;
import kr.yeonkyung.blog.provider.exception.ApiClientErrorException;
import kr.yeonkyung.blog.provider.exception.ApiServerErrorException;
import kr.yeonkyung.blog.provider.kakao.KakaoApiService;
import kr.yeonkyung.blog.provider.naver.NaverApiService;
import kr.yeonkyung.common.exception.InternalServerErrorException;
import kr.yeonkyung.common.response.PagedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class BlogServiceTest {

    @Mock
    KakaoApiService kakaoApiService;
    @Mock
    NaverApiService naverApiService;

    BlogService blogService;
    List<ApiService> apiServiceList;

    @BeforeEach
    public void setUp() {
        apiServiceList = Arrays.asList(kakaoApiService, naverApiService);
        blogService = new BlogService(apiServiceList);
    }

    @Test
    void API_ClientError_발생() {
        // given
        doThrow(ApiClientErrorException.class)
                .when(kakaoApiService).searchBlog("query", "accuracy", 1, 10);
        // when
        // then
        assertThrows(ApiClientErrorException.class, () -> {
            blogService.searchBlog("query", "accuracy", 1, 10);
        });
        // NaverApiService를 호출하지 않고 BAD_REQUEST 응답
        verifyNoInteractions(naverApiService);
    }

    @Test
    void 카카오API서버_장애_발생() {
        // given
        doThrow(ApiServerErrorException.class)
                .when(kakaoApiService).searchBlog("query", "accuracy", 1, 10);
        PagedResponse response = mock(PagedResponse.class);
        doReturn(response)
                .when(naverApiService).searchBlog("query", "accuracy", 1, 10);
        // when
        // then
        assertEquals(response, blogService.searchBlog("query", "accuracy", 1, 10));
        // NaverApiService 호출
        verify(naverApiService).searchBlog("query", "accuracy", 1, 10);
    }

    @Test
    void 카카오API서버_정상_동작_시_네이버API서버_미호출_확인() {
        // given
        PagedResponse response = mock(PagedResponse.class);
        doReturn(response)
                .when(kakaoApiService).searchBlog("query", "accuracy", 1, 10);
        // when
        // then
        assertEquals(response, blogService.searchBlog("query", "accuracy", 1, 10));
        // 카카오 API 서버가 정상 동작해서 결과를 제대로 return하면 네이버 API 서버는 호출하지 않는다.
        verifyNoInteractions(naverApiService);
    }

    @Test
    void 카카오_네이버_API서버_모두_장애_발생() {
        doThrow(ApiServerErrorException.class)
                .when(kakaoApiService).searchBlog("query", "accuracy", 1, 10);
        doThrow(ApiServerErrorException.class)
                .when(naverApiService).searchBlog("query", "accuracy", 1, 10);
        // when
        // then
        // 모든 API 서버가 사용 불가한 경우 InternalServerErrorException을 던진다.
        String message = assertThrows(InternalServerErrorException.class, () -> {
            blogService.searchBlog("query", "accuracy", 1, 10);
        }).getMessage();
        assertEquals("Blog Search Service is not available", message);
    }

    @Test
    void 내부_서버_장애_발생() {
        doThrow(InternalServerErrorException.class)
                .when(kakaoApiService).searchBlog("query", "accuracy", 1, 10);
        doThrow(InternalServerErrorException.class)
                .when(naverApiService).searchBlog("query", "accuracy", 1, 10);
        // when
        // then
        // 내부 서버의 문제로 API 호출이 불가한 경우 InternalServerErrorException을 던진다.
        String message = assertThrows(InternalServerErrorException.class, () -> {
            blogService.searchBlog("query", "accuracy", 1, 10);
        }).getMessage();
        assertEquals("Blog Search Service is not available", message);
    }
}
