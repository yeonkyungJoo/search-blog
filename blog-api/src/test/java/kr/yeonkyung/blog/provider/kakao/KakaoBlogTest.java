package kr.yeonkyung.blog.provider.kakao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KakaoBlogTest {

    private KakaoResponse.BlogResult document = new KakaoResponse.BlogResult();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(document, "title", "title");
        ReflectionTestUtils.setField(document, "contents", "contents");
        ReflectionTestUtils.setField(document, "url", "url");
        ReflectionTestUtils.setField(document, "blogName", "blog name");
        ReflectionTestUtils.setField(document, "datetime", "2017-05-07T18:50:07.000+09:00");
    }

    @Test
    void response_체크() {
        KakaoBlog blog = new KakaoBlog(document);
        assertAll(
                () -> assertEquals(document.getTitle(), blog.getTitle()),
                () -> assertEquals(document.getContents(), blog.getContents()),
                () -> assertEquals(document.getUrl(), blog.getUrl()),
                () -> assertEquals(document.getBlogName(), blog.getBlogName()),
                () -> assertEquals("20170507", blog.getPostDate())
        );
    }
}
