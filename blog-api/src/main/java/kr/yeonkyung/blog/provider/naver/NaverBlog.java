package kr.yeonkyung.blog.provider.naver;

import kr.yeonkyung.blog.Blog;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NaverBlog implements Blog {
// 네이버 API 서비스를 통해 얻은 블로그 검색 결과를 Response에 맞게 매핑
    
    private final NaverResponse.Item item;

    @Override
    public String getTitle() {
        return item.getTitle();
    }

    @Override
    public String getContents() {
        return item.getDescription();
    }

    @Override
    public String getUrl() {
        return item.getLink();
    }

    @Override
    public String getBlogName() {
        return item.getBloggerName();
    }

    @Override
    public String getPostDate() {
        return item.getPostdate();
    }
}
