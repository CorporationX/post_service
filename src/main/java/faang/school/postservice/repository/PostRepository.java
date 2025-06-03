package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p WHERE p.authorId = :authorId AND p.published = false AND p.deleted = false")
    List<Post> findDraftsByAuthorId(long authorId);

    @Query("SELECT p FROM Post p WHERE p.projectId = :projectId AND p.published = false AND p.deleted = false")
    List<Post> findDraftsByProjectId(long projectId);

    @Query("SELECT p FROM Post p WHERE p.authorId = :authorId AND p.published = true AND p.deleted = false")
    List<Post> findPublishedByAuthorId(long authorId);

    @Query("SELECT p FROM Post p WHERE p.projectId = :projectId AND p.published = true AND p.deleted = false")
    List<Post> findPublishedByProjectId(long projectId);

    @Query("SELECT p FROM Post p WHERE p.isCorrected = false")
    Page<Post> findUncorrectedPosts(Pageable pageable);

    @Query("SELECT p FROM Post p JOIN p.hashtags h WHERE h.tag = :tag AND p.deleted = false ORDER BY p.publishedAt DESC")
    Page<Post> findPostsByHashtag(Pageable pageable, String tag);

    @Query("SELECT p FROM Post p WHERE p.verified = false")
    Stream<Post> streamByVerifiedFalse();

    @Query("SELECT p FROM Post p WHERE p.authorId IN :authorIds AND p.published = true ORDER BY p.publishedAt DESC")
    List<Post> findByAuthorIdsAndPublishedTrue(@Param("authorIds") Set<Long> authorIds, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.comments WHERE p.id IN :ids")
    List<Post> findAllByIdWithComments(@Param("ids") Set<Long> ids);

    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.views = p.views + 1 WHERE p.id = :id")
    int incrementViews(Long id);
}
