package kr.yeonkyung.blog.provider.naver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NaverBlogTest {

    private NaverResponse.Item item = new NaverResponse.Item();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(item, "title", "title");
        ReflectionTestUtils.setField(item, "link", "link");
        ReflectionTestUtils.setField(item, "description", "description");
        ReflectionTestUtils.setField(item, "bloggerName", "blogName");
        ReflectionTestUtils.setField(item, "postdate", "20230131");
    }

    @Test
    void response_체크() {
        NaverBlog blog = new NaverBlog(item);
        assertAll(
                () -> assertEquals(item.getTitle(), blog.getTitle()),
                () -> assertEquals(item.getLink(), blog.getUrl()),
                () -> assertEquals(item.getDescription(), blog.getContents()),
                () -> assertEquals(item.getBloggerName(), blog.getBlogName()),
                () -> assertEquals(item.getPostdate(), blog.getPostDate())
        );
    }
}
