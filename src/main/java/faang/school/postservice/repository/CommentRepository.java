package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    int countAllByVerifiedDateIsNull();

    Page<Comment> findByVerifiedDateIsNull(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Comment c SET c.verified = :verified, c.verifiedDate = :now WHERE c.id IN :ids")
    void updateVerifiedStatusAndDateByIds(List<Long> ids, LocalDateTime now, boolean verified);
}
