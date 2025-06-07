package faang.school.postservice.repository.comment;

import faang.school.postservice.exception.file.FileNotFoundException;
import faang.school.postservice.model.CommentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentImageRepository extends JpaRepository<CommentImage, Long> {
    default CommentImage getByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new FileNotFoundException("Image not found"));
    }
}
