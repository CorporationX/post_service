package faang.school.postservice.repository;

import faang.school.postservice.model.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "feedRepository")
public interface FeedRepository extends JpaRepository<Feed, Long> {

    @Query(nativeQuery = true, value = """
        select
            *
        from feed f
        where f.user_id = :userId
            and f.post_id > :postId
        order by created_at
        limit :feedAmount
        """)
    List<Feed> findFeedsByUserId(long userId, Long postId, long feedAmount);
}
