package kr.yeonkyung.blog;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SearchKeywordServiceTest {

    @InjectMocks
    SearchKeywordService searchKeywordService;
    @Mock
    SearchKeywordRepository searchKeywordRepository;

    @Test
    void 처음_검색되는_키워드로_검색() {
        // given
        String query = "test";
        doReturn(Optional.empty())
                .when(searchKeywordRepository).findWithOptimisticLockByKeyword(query);
        // when
        searchKeywordService.saveSearchKeyword(query);
        // then
        verify(searchKeywordRepository).saveAndFlush(any(SearchKeyword.class));
    }

    @Test
    void 이미_검색된_키워드로_검색() {
        // given
        String query = "test";

        SearchKeyword searchKeyword = mock(SearchKeyword.class);
        doReturn(1)
                .when(searchKeyword).getCount();
        doReturn(Optional.of(searchKeyword))
                .when(searchKeywordRepository).findWithOptimisticLockByKeyword(query);
        // when
        searchKeywordService.saveSearchKeyword(query);
        // then
        verify(searchKeyword).setCount(2);
        verify(searchKeywordRepository).saveAndFlush(any(SearchKeyword.class));
    }

    @Test
    void 인기_검색어_리스트_조회() {
        // given
        // when
        searchKeywordService.getPopularSearchKeywordList();
        // then
        verify(searchKeywordRepository).findTop10OrderByCountDesc();
    }
}
