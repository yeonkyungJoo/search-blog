package kr.yeonkyung.blog;

import kr.yeonkyung.blog.provider.exception.ApiClientErrorException;
import kr.yeonkyung.blog.provider.exception.ApiServerErrorException;
import kr.yeonkyung.common.exception.InternalServerErrorException;
import kr.yeonkyung.common.response.PagedResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestClientException;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BlogController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
class BlogControllerTest {

    private final String BASE_URL = "/blog";

    @Autowired
    MockMvc mockMvc;
    @MockBean
    BlogService blogService;
    @MockBean
    SearchKeywordSaveService searchKeywordSaveService;
    @MockBean
    SearchKeywordService searchKeywordService;

    @Test
    void query없이_블로그_검색() throws Exception {
        // given
        // when
        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/search"))
                .andDo(print());
        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
        verifyNoInteractions(searchKeywordSaveService);
        verifyNoInteractions(blogService);
    }

    @Test
    void 유효하지_않은_sort값으로_블로그_검색() throws Exception {
        // given
        // when
        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/search")
                        .param("query", "test")
                        .param("sort", "acc"))
                .andDo(print());
        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Invalid sort value"));
        verifyNoInteractions(searchKeywordSaveService);
        verifyNoInteractions(blogService);
    }

    @Test
    void 유효하지_않은_page값으로_블로그_검색() throws Exception {
        // given
        // when
        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/search")
                        .param("query", "test")
                        .param("page", "0"))
                .andDo(print());
        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Invalid page value"));
        verifyNoInteractions(searchKeywordSaveService);
        verifyNoInteractions(blogService);
    }

    @Test
    void 유효하지_않은_size값으로_블로그_검색() throws Exception {
        // given
        // when
        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/search")
                        .param("query", "test")
                        .param("size", "51"))
                .andDo(print());
        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Invalid size value"));
        verifyNoInteractions(searchKeywordSaveService);
        verifyNoInteractions(blogService);
    }

    @Test
    void 블로그_검색() throws Exception {
        // given
        String query = "test";
        doNothing()
                .when(searchKeywordSaveService).saveSearchKeyword(query);
        PagedResponse<Blog> pagedResponse = new PagedResponse<>(1, 1, 10,
                List.of(new Blog() {
                            @Override
                            public String getTitle() {
                                return "title";
                            }

                            @Override
                            public String getContents() {
                                return "contents";
                            }

                            @Override
                            public String getUrl() {
                                return "url";
                            }

                            @Override
                            public String getBlogName() {
                                return "blogName";
                            }

                            @Override
                            public String getPostDate() {
                                return "postDate";
                            }
                        }));
        doReturn(pagedResponse)
                .when(blogService).searchBlog(query, "accuracy", 1, 10);

        // when
        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/search")
                        .param("query", "test"))
                .andDo(print());
        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.total").value(1))
                .andExpect(jsonPath("$.meta.page").value(1))
                .andExpect(jsonPath("$.meta.size").value(10))
                .andExpect(jsonPath("$.items..title").value("title"))
                .andExpect(jsonPath("$.items..contents").value("contents"))
                .andExpect(jsonPath("$.items..url").value("url"))
                .andExpect(jsonPath("$.items..blogName").value("blogName"))
                .andExpect(jsonPath("$.items..postDate").value("postDate"));
        verify(searchKeywordSaveService).saveSearchKeyword(query);
        verify(blogService).searchBlog(query, "accuracy", 1, 10);
    }

    @Test
    void API_ClientError_발생() throws Exception {
        // given
        String query = "test";
        doNothing()
                .when(searchKeywordSaveService).saveSearchKeyword(query);
        ApiClientErrorException ex = new ApiClientErrorException(HttpStatus.BAD_REQUEST, "page is less than min", mock(RestClientException.class));
        doThrow(ex)
                .when(blogService).searchBlog(query, "accuracy", 1, 10);

        // when
        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/search")
                        .param("query", "test"))
                .andDo(print());
        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.name()));
        verify(searchKeywordSaveService).saveSearchKeyword(query);
        verify(blogService).searchBlog(query, "accuracy", 1, 10);
    }

    @Test
    void API_ServerError_발생() throws Exception {
        // given
        String query = "test";
        doNothing()
                .when(searchKeywordSaveService).saveSearchKeyword(query);
        doThrow(ApiServerErrorException.class)
                .when(blogService).searchBlog(query, "accuracy", 1, 10);

        // when
        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/search")
                        .param("query", "test"))
                .andDo(print());
        // then
        resultActions
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));
        verify(searchKeywordSaveService).saveSearchKeyword(query);
        verify(blogService).searchBlog(query, "accuracy", 1, 10);
    }

    @Test
    void 블로그_검색_시_내부_서버_에러_발생() throws Exception {
        // given
        String query = "test";
        doNothing()
                .when(searchKeywordSaveService).saveSearchKeyword(query);
        doThrow(InternalServerErrorException.class)
                .when(blogService).searchBlog(query, "accuracy", 1, 10);

        // when
        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/search")
                        .param("query", "test"))
                .andDo(print());
        // then
        resultActions
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));
        verify(searchKeywordSaveService).saveSearchKeyword(query);
        verify(blogService).searchBlog(query, "accuracy", 1, 10);
    }

    @Test
    void 인기_검색어_리스트_조회() throws Exception {
        // given
        SearchKeyword searchKeyword1 = new SearchKeyword("keyword1");
        searchKeyword1.setCount(1);
        SearchKeyword searchKeyword2 = new SearchKeyword("keyword2");
        searchKeyword2.setCount(3);
        doReturn(List.of(searchKeyword1, searchKeyword2))
                .when(searchKeywordService).getPopularSearchKeywordList();
        // when
        ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/get-popular-search-keywords"))
                .andDo(print());
        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.searchKeywordList[0].keyword").value(searchKeyword1.getKeyword()))
                .andExpect(jsonPath("$.searchKeywordList[0].count").value(searchKeyword1.getCount()))
                .andExpect(jsonPath("$.searchKeywordList[1].keyword").value(searchKeyword2.getKeyword()))
                .andExpect(jsonPath("$.searchKeywordList[1].count").value(searchKeyword2.getCount()));
        verify(searchKeywordService).getPopularSearchKeywordList();
    }
}
