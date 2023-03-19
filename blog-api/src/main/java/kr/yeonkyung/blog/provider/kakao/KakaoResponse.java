package kr.yeonkyung.blog.provider.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class KakaoResponse {
// 카카오 API Response 구조 (파싱을 위한 클래스)

    @Getter
    static class SearchMeta {
        @JsonProperty("total_count")
        private Integer totalCount;
        @JsonProperty("pageable_count")
        private Integer pageableCount;
        @JsonProperty("is_end")
        private Boolean isEnd;
    }

    @Getter
    static class BlogResult {
        private String title;
        private String contents;
        private String url;
        @JsonProperty("blogname")
        private String blogName;
        private String thumbnail;
        private String datetime;
    }

    private SearchMeta meta;
    private List<BlogResult> documents;
}
