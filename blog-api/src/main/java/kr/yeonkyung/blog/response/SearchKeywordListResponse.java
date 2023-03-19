package kr.yeonkyung.blog.response;

import kr.yeonkyung.blog.SearchKeyword;
import lombok.Getter;

import java.util.List;

@Getter
public class SearchKeywordListResponse {

    private List<SearchKeyword> searchKeywordList;

    public SearchKeywordListResponse(List<SearchKeyword> searchKeywordList) {
        this.searchKeywordList = searchKeywordList;
    }
}
