package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends CrudRepository<Like, Long> {

    void deleteByUserIdAndPostId(Long userId, Long postId);

    void deleteByUserIdAndCommentId( Long userId, Long commentId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    boolean existsByUserIdAndCommentId(Long userId, Long commentId);

    List<Like> findByPostId(long postId);

    List<Like> findByCommentId(long commentId);
}
