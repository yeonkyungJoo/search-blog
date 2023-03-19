package kr.yeonkyung.blog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface SearchKeywordRepository extends JpaRepository<SearchKeyword, String> {

    @Lock(LockModeType.OPTIMISTIC)
    Optional<SearchKeyword> findWithOptimisticLockByKeyword(String keyword);

    @Query(value = "select * from search_keyword order by count desc limit 10", nativeQuery = true)
    List<SearchKeyword> findTop10OrderByCountDesc();
}
