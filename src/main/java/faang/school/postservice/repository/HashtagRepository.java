package faang.school.postservice.repository;

import faang.school.postservice.model.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    @Query(nativeQuery = true, value = """
            SELECT h.id, h.hashtag FROM hashtags h
            JOIN post_hashtag ph
            ON h.id = ph.hashtag_id
            GROUP BY h.id, h.hashtag
            ORDER BY COUNT(ph.post_id) DESC
            LIMIT 10
            """)
    List<Hashtag> getTop10Popular();
}
