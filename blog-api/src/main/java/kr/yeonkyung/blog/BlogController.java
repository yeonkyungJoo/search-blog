package kr.yeonkyung.blog;

import kr.yeonkyung.blog.response.SearchKeywordListResponse;
import kr.yeonkyung.common.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/blog")
@RequiredArgsConstructor
@RestController
public class BlogController {

    private final BlogService blogService;
    private final SearchKeywordSaveService searchKeywordSaveService;
    private final SearchKeywordService searchKeywordService;

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<Blog>> searchBlog(@RequestParam String query,
                                                          @RequestParam(defaultValue = "accuracy") String sort,
                                                          @RequestParam(defaultValue = "1") Integer page,
                                                          @RequestParam(defaultValue = "10") Integer size) {

        // 입력값 체크
        if (!(sort.equalsIgnoreCase("accuracy") || sort.equalsIgnoreCase("recency"))) {
            throw new IllegalArgumentException("Invalid sort value");
        }
        if (page < 1 || page > 20) {
            throw new IllegalArgumentException("Invalid page value");
        }
        if (size < 1 || size > 50) {
            throw new IllegalArgumentException("Invalid size value");
        }

        searchKeywordSaveService.saveSearchKeyword(query);
        PagedResponse<Blog> response = blogService.searchBlog(query, sort, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-popular-search-keywords")
    public ResponseEntity<SearchKeywordListResponse> listPopularSearchKeyword() {
        List<SearchKeyword> popularSearchKeywordList = searchKeywordService.getPopularSearchKeywordList();
        return ResponseEntity.ok(new SearchKeywordListResponse(popularSearchKeywordList));
    }
}
