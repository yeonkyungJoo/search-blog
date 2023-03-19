package kr.yeonkyung.blog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "search_keyword")
@Entity
public class SearchKeyword {

    @Id
    @Column(name = "keyword", unique = true, nullable = false, updatable = false)
    private String keyword;

    @Column(name = "count", nullable = false)
    private int count = 0;

    // 낙관적 락을 위한 version 컬럼
    @JsonIgnore
    @Version
    private Long version = 0L;

    public SearchKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
