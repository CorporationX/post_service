package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false" +
            " AND p.deleted = false" +
            " AND p.scheduledAt < CURRENT_TIMESTAMP" +
            " AND p.spellCheckCompleted = false" )
    List<Post> findReadyToPublish();

    @Query(value = "SELECT p FROM Post p JOIN p.hashtags h WHERE h.id = :hashtagId", nativeQuery = true)
    List<Post> findByHashtagId(Long hashtagId);

    Page<Post> findByHashtagsContent(String content, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM post_album WHERE album_id = :albumId AND post_id = :postId")
    boolean existsInAlbum(long albumId, long postId);
}
