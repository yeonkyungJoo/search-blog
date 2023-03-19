package kr.yeonkyung.blog;

import kr.yeonkyung.blog.provider.ApiService;
import kr.yeonkyung.blog.provider.exception.ApiClientErrorException;
import kr.yeonkyung.blog.provider.exception.ApiServerErrorException;
import kr.yeonkyung.common.exception.InternalServerErrorException;
import kr.yeonkyung.common.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BlogService {

    // ApiService를 구현한 KakaoApiService, NaverApiService 주입
    private final List<ApiService> apiServiceList;

    public PagedResponse<Blog> searchBlog(final String query, String sort, Integer page, Integer size) throws ApiClientErrorException {

        for (ApiService apiService : apiServiceList) {
            try {
                // searchBlog() 실행 도중 ApiClientErrorException가 발생하면 다른 API 서버 호출 없이 바로 response
                return apiService.searchBlog(query, sort, page, size);
            } catch (ApiServerErrorException | InternalServerErrorException ignored) {
                // 서버(API서버, 내부 서버) 에러는 ignore해서 다른 API 서버 이용
                log.error("서버 에러", ignored);
            }
        }
        // API Service 모두 불가능한 경우
        throw new InternalServerErrorException("Blog Search Service is not available");
    }
}
