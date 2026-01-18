package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedDbRepository extends JpaRepository<Post, Long> {

    @Query("""
                select p
                from Post p
                where p.published = true
                  and p.deleted = false
                  and p.authorId in :followeeIds
                order by p.createdAt desc, p.id desc
            """)
    List<Post> findFeedPostsFirstPage(
            @Param("followeeIds") List<Long> followeeIds,
            Pageable pageable
    );

    @Query("""
                select p
                from Post p
                where p.published = true
                  and p.deleted = false
                  and p.authorId in :followeeIds
                  and (
                      p.createdAt < :cursorCreatedAt
                      or (p.createdAt = :cursorCreatedAt and p.id < :cursorPostId)
                  )
                order by p.createdAt desc, p.id desc
            """)
    List<Post> findFeedPostsAfterCursor(
            @Param("followeeIds") List<Long> followeeIds,
            @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
            @Param("cursorPostId") Long cursorPostId,
            Pageable pageable
    );
}
