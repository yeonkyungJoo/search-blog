package kr.yeonkyung.blog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SearchKeywordSaveService {

    private final SearchKeywordService searchKeywordService;

    // 트래픽이 많다는 점을 고려
    // 빠른 검색을 위해 검색 키워드 저장은 비동기 처리
    @Async("asyncExecutor")
    public void saveSearchKeyword(final String query) {
        try {
            log.info("query : {}", query);
            searchKeywordService.saveSearchKeyword(query);
        } catch (Exception e) {
            log.error("exception : {}", e.getMessage());
            // 예외 발생 시 재시도
            saveSearchKeyword(query);
        }
    }
}
