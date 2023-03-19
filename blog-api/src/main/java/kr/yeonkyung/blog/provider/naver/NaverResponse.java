package kr.yeonkyung.blog.provider.naver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class NaverResponse {
// 네이버 API Response 구조 (파싱을 위한 클래스)

    @Getter
    static class Item {
        private String title;
        private String link;
        private String description;
        @JsonProperty("bloggername")
        private String bloggerName;
        @JsonProperty("bloggerlink")
        private String bloggerLink;
        private String postdate;
    }

    private String lastBuildDate;
    private int total;
    private int start;
    private int display;
    private List<Item> items;
}
