package faang.school.postservice.repository.comment;

import faang.school.postservice.model.comment.CommentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentImageRepository extends JpaRepository<CommentImage, Long> {
    Optional<CommentImage> findByIdAndCommentId(Long imageId, Long commentId);
}
