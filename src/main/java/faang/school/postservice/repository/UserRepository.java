package faang.school.postservice.repository;

import faang.school.postservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(nativeQuery = true,
            value = """
    select p.id from subscription s
join post p on s.followee_id = p.author_id where
s.follower_id = :user order by p.published_at limit :limit;
"""
    )
    List<Long> getPostIdsForFeedById(@Param("userId") long userId, @Param("limit") int limit);
}
