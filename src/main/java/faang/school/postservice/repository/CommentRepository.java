package faang.school.postservice.repository;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import feign.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        UPDATE comments 
        SET content = :content, 
            large_image_file_key = :largeImageFileKey,
            small_image_file_key = :smallImageFileKey,
            updated_at = NOW()
        WHERE id = :commentId
            AND post_id = :postId 
            AND author_id = :authorId
        RETURNING *
        """)
    Optional<Comment> update(@Param("commentId") Long commentId,
                             @Param("postId") Long postId,
                             @Param("authorId") Long authorId,
                             @Param("content") String content,
                             @Param("largeImageFileKey") String largeImageFileKey,
                             @Param("smallImageFileKey") String smallImageFileKey);

    Optional<Comment> findByIdAndPostId(Long postId, Long commentId);

    default Comment findByIdAndPostIdOrThrow(Long postId, Long commentId) {
        return findByIdAndPostId(postId, commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий с id " + commentId + " не найден"));
    }
}
