package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Transactional
    List<Post> findAllByVerifiedAtIsNull();

    @Transactional
    @Query(value = "select * from post p " +
            "where p.published = true " +
            "and p.deleted = false " +
            "and p.author_id in :authorIds " +
            "and p.id >= :startPostId " +
            "order by p.id desc limit :batchSize ", nativeQuery = true)
    List<Post> findPostsByAuthorIds(@Param("authorIds") List<Long> authorIds, @Param("startPostId") long startPostId,
                                      @Param("batchSize") long batchSize);
}