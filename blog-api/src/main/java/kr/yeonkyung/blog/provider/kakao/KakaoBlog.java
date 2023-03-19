package kr.yeonkyung.blog.provider.kakao;

import kr.yeonkyung.blog.Blog;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class KakaoBlog implements Blog {
// 카카오 API 서비스를 통해 얻은 블로그 검색 결과를 Response에 맞게 매핑
    
    private final KakaoResponse.BlogResult document;

    @Override
    public String getTitle() {
        return document.getTitle();
    }

    @Override
    public String getContents() {
        return document.getContents();
    }

    @Override
    public String getUrl() {
        return document.getUrl();
    }

    @Override
    public String getBlogName() {
        return document.getBlogName();
    }

    @Override
    public String getPostDate() {
        return LocalDate.parse(document.getDatetime(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).format(DateTimeFormatter.BASIC_ISO_DATE);
    }
}
