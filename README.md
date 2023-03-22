# 개요

### 프로젝트 구성
- yeonkyung (rootProject)
  - common
    - 모듈에 공통적으로 사용되는 클래스
    - Exception, Response 클래스
  - blog-api
    - 블로그 서비스 모듈
  - server
    - Main Class

### 기술 스택 및 라이브러리
- Java 17, Spring Boot, Gradle, JPA, JUnit5, H2
- lombok (어노테이션 기반 코드 자동 생성으로 생산성 향상)

### 다운로드 링크
- https://drive.google.com/uc?export=download&id=1-I5k-RnSzNrNvGiEV-wUUn0ghZCDcBX7
```
java - jar server.jar
```

---
# API

### 1. 블로그 검색
```
GET /blog/search
```
<br>

#### Request
원하는 검색어와 함께 결과 형식 파라미터를 선택적으로 추가할 수 있습니다.  
###### Parameter
| 값       |  Type   | Description                                            |  Required  |
|---------|:-------:|--------------------------------------------------------|:----------:|
| `query` | String  | 검색을 원하는 질의어                                            |     O      |
| `sort`  | String  | 결과 정렬 방식 / accuracy(정확도순), recency(최신순), 기본 값 accuracy |     X      |
| `page`  | Integer | 결과 페이지 번호 / 1 ~ 20 사이의 값, 기본 값 1                       |     X      |
| `size`  | Integer | 한 페이지에 보여질 문서 수 / 1 ~ 50 사이의 값, 기본 값 10                |     X      |
<br>

#### Response
응답 바디는 meta, items로 구성된 JSON 객체입니다.
###### meta
| Name    |  Type   | Description       |
|---------|:-------:|-------------------|
| `total` | Integer | (노출 가능한) 총 데이터 수  |
| `page`  | Integer | 현재 페이지 번호         |
| `size`  | Integer | 한 페이지에 보여지는 데이터 수 |
###### items
| Name       |  Type  | Description         |
|------------|:------:|---------------------|
| `title`    | String | 블로그 글 제목            |
| `contents` | String | 블로그 글 요약            |
| `url`      | String | 블로그 글 URL           |
| `blogName` | String | 블로그 이름              |
| `postDate` | String | 블로그 작성 시간(YYYYMMDD) |
<br>

#### Example
###### Request
```
curl "http://localhost:8080/blog/search?query=kakao&sort=accuracy&page=1&size=5"
```
###### Response
```
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Tue, 21 Mar 2023 12:21:02 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  "meta": {
    "total": 5750692,
    "page": 1,
    "size": 5
  },
  "items": [
    {
      "contents": "좋은 연휴시간 되세요 ... ",
      "title": "국민주 카카오 <b>kakao</b>",
      "url": "https://blog.naver.com",
      "blogName": "경제지표",
      "postDate": "20221007"
    },
    ...
  ]
}
```
<br>

---

### 2. 인기 검색어 목록
```
GET /blog/get-popular-search-keywords
```
<br>

#### Request
파라미터가 없습니다.
<br>

#### Response
응답 바디는 searchKeywordList로 구성된 JSON 객체이며,
사용자들이 많이 검색한 순서대로 최대 10개까지 제공됩니다.
###### searchKeywordList
| Name      |   Type   | Description |
|-----------|:--------:|-------------|
| `keyword` | String   | 검색된 키워드 |
| `count`   | Integer  | 검색 횟수    |
<br>

#### Example
###### Request
```
curl "http://localhost:8080/blog/get-popular-search-keywords"
```
###### Response
```
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Wed, 22 Mar 2023 11:42:54 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  "searchKeywordList": [
    {
      "keyword": "카카오뱅크",
      "count": 3
    },
    ...
  ]
}
```
