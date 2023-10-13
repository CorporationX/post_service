package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    List<Post> findAllByVerifiedDateIsNull();

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.spellCheck = true AND p.spellCheckedAt IS NULL")
    List<Post> findReadyToSpellCorrection();

    @Query("SELECT p FROM Post p WHERE p.authorId = :authorId AND p.published = false AND p.deleted = false")
    List<Post> findDraftsByAuthorId(long authorId);

    @Query("SELECT p FROM Post p WHERE p.projectId = :projectId AND p.published = false AND p.deleted = false")
    List<Post> findDraftsByProjectId(long projectId);

    @Query("SELECT p FROM Post p WHERE p.authorId = :authorId AND p.published = true AND p.deleted = false")
    List<Post> findPublishedPostsByAuthorId(long authorId);

    @Query("SELECT p FROM Post p WHERE p.projectId = :projectId AND p.published = true AND p.deleted = false")
    List<Post> findPublishedPostsByProjectId(long projectId);

    @Query("SELECT p FROM Post p WHERE p.verified = false")
    List<Post> findUnverifiedPosts();
}
