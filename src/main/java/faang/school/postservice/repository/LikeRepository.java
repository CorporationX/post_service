package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    @Transactional
    void deleteByPostIdAndUserId(long postId, long userId);

    @Transactional
    void deleteByCommentIdAndUserId(long commentId, long userId);

    Optional<Like> findByPostIdAndUserId(long postId, long userId);

    Optional<Like> findByCommentIdAndUserId(long commentId, long userId);

    Optional<List<Like>> findAllByPostId(long postId);
}
