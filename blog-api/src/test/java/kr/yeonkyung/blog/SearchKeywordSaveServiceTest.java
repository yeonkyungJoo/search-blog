package kr.yeonkyung.blog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(properties = {
        "api.provider.kakao.url=url",
        "api.provider.kakao.token=token",
        "api.provider.naver.url=url",
        "api.provider.naver.client-id=client_id",
        "api.provider.naver.client-secret=client_secret"})
class SearchKeywordSaveServiceTest {

    @Autowired
    SearchKeywordSaveService searchKeywordSaveService;
    @Autowired
    SearchKeywordRepository searchKeywordRepository;

    @BeforeEach
    void setUp() {
        searchKeywordRepository.deleteAll();
    }

    @Test
    void 검색() {
        // given
        // when
        String query = "test1";
        searchKeywordSaveService.saveSearchKeyword(query);

        // then
        SearchKeyword searchKeyword = searchKeywordRepository.findById(query)
                .orElseThrow(RuntimeException::new);
        assertEquals(1, searchKeyword.getCount());
    }

    @Test
    void 동일한_키워드로_100번_검색() throws InterruptedException {
        // given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        String query = "test2";
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    searchKeywordSaveService.saveSearchKeyword(query);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        SearchKeyword searchKeyword = searchKeywordRepository.findById(query)
                .orElseThrow(RuntimeException::new);
        assertEquals(100, searchKeyword.getCount());
    }
}
