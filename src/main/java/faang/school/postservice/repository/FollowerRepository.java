package faang.school.postservice.repository;

import faang.school.postservice.model.Follower;
import faang.school.postservice.model.composite_id.FollowerId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, FollowerId> {
    @Query("SELECT f.id.followerId FROM Follower f WHERE f.id.authorId = :authorId")
    List<Long> findAllAuthorFollowerIds(long authorId, Pageable pageable);
}
