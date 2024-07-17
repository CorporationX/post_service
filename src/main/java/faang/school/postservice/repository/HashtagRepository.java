package faang.school.postservice.repository;

import faang.school.postservice.model.Hashtag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    @Query("SELECT h FROM Hashtag h ORDER BY SIZE(h.posts) DESC")
    List<Hashtag> findTopXPopularHashtags(Pageable pageable);
}
