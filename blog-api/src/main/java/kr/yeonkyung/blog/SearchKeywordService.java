package kr.yeonkyung.blog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SearchKeywordService {

    private final SearchKeywordRepository searchKeywordRepository;

    @Transactional
    public void saveSearchKeyword(final String query) {
        // 낙관적 락을 이용한 조회 후 데이터 변경
        SearchKeyword searchKeyword = searchKeywordRepository.findWithOptimisticLockByKeyword(query)
                .orElseGet(() -> new SearchKeyword(query));
        searchKeyword.setCount(searchKeyword.getCount() + 1);
        searchKeywordRepository.saveAndFlush(searchKeyword);
    }

    @Transactional(readOnly = true)
    public List<SearchKeyword> getPopularSearchKeywordList() {
        return searchKeywordRepository.findTop10OrderByCountDesc();
    }
}
