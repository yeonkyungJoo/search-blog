package kr.yeonkyung.blog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SearchKeywordSaveService {

    private final SearchKeywordService searchKeywordService;

    public void saveSearchKeyword(final String query) {
        try {
            searchKeywordService.saveSearchKeyword(query);
        } catch (Exception e) {
            // 예외 발생 시 재시도
            saveSearchKeyword(query);
        }
    }
}
