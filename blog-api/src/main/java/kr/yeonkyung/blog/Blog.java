package kr.yeonkyung.blog;

public interface Blog {
// 블로그 검색 결과 Response 구조
// 각 API 서버의 response 구조가 다르므로, 공통된 인터페이스를 선언해 각각 구현하도록 작성

    String getTitle();
    String getContents();
    String getUrl();
    String getBlogName();
    String getPostDate();
}
