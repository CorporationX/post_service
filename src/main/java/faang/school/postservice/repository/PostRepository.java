package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import lombok.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Long>, PostRepositoryCustom {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    Optional<Post> findByIdAndDeletedFalse(@NonNull Long postId);

    List<Post> findAllByPublishedFalse();

    @Query("""
                select p from Post p
                where p.published = true
                  and p.deleted = false
                  and p.authorId in :authorIds
                order by p.id desc
            """)
    List<Post> findRecentByAuthors(@Param("authorIds") Collection<Long> authorIds,
                                   Pageable pageable);

    default List<Post> findRecentByAuthors(Collection<Long> authorIds, int limit) {
        return findRecentByAuthors(authorIds, PageRequest.of(0, limit));
    }

    @Query("""
                select p from Post p
                where p.published = true
                  and p.deleted = false
                  and p.authorId in :authorIds
                  and p.id < :afterId
                order by p.id desc
            """)
    List<Post> findRecentByAuthorsAfterId(@Param("authorIds") Collection<Long> authorIds,
                                          @Param("afterId") Long afterId,
                                          Pageable pageable);

    default List<Post> findRecentByAuthorsAfterId(Collection<Long> authorIds, Long afterId, int limit) {
        return findRecentByAuthorsAfterId(authorIds, afterId, PageRequest.of(0, limit));
    }

    @Query("""
                select distinct p.authorId
                from Post p
                where p.published = true
                  and p.deleted = false
            """)
    List<Long> findDistinctAuthorIdsOfPublished();

    @Query(value = "SELECT * FROM post WHERE id IN :ids", nativeQuery = true)
    List<Post> getByIds(List<Long> ids);
}
