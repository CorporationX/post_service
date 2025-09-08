package faang.school.postservice.repository;

import faang.school.postservice.model.Follower;
import faang.school.postservice.model.composite_id.FollowerId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, FollowerId> {
    @Query("SELECT f.id.followerId FROM Follower f WHERE f.id.authorId = :authorId")
    Slice<Long> findAllAuthorFollowerIds(long authorId, Pageable pageable);

    @Query("SELECT COUNT(f.id.followerId) FROM Follower f WHERE f.id.authorId = :authorId")
    long countByAuthorId(long authorId);
}
