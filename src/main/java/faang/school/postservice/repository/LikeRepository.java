package faang.school.postservice.repository;

import faang.school.postservice.dto.feed.LikeCountDto;
import faang.school.postservice.model.Like;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends CrudRepository<Like, Long> {
    @Transactional
    void deleteByPostIdAndUserId(long postId, long userId);

    @Transactional
    void deleteByCommentIdAndUserId(long commentId, long userId);

    Optional<Like> findByPostIdAndUserId(long postId, long userId);

    Optional<Like> findByCommentIdAndUserId(long commentId, long userId);
    List<Like> findByIdIn(List<Long> ids);

    @Query("""
        SELECT l.post.id AS postId, COUNT(l) AS likeCount
        FROM Like l
        WHERE l.post.id IN :postIds
        GROUP BY l.post.id
    """)
    List<LikeCountDto> countLikesByPostIds(@Param("postIds") List<Long> postIds);
}
