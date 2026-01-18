package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface FeedDbRepository extends Repository<Post, Long> {

    @Query("""
            SELECT p
            FROM Post p
            WHERE p.published = true
              AND p.deleted = false
              AND p.authorId IN :authorIds
              AND (
                   :cursorCreatedAt IS NULL
                   OR p.createdAt < :cursorCreatedAt
                   OR (p.createdAt = :cursorCreatedAt AND p.id < :cursorId)
              )
            ORDER BY p.createdAt DESC, p.id DESC
            """)
    List<Post> findFeedPosts(
            @Param("authorIds") Collection<Long> authorIds,
            @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}