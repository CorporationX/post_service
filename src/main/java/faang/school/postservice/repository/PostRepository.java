package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import feign.Param;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Transactional
    @Query(value = "select p from Post p " +
            "left join fetch p.comments c " +
            "where p.published = true " +
            "and p.deleted = false " +
            "and p.authorId in :authorIds " +
            "and p.id >= :startPostId " +
            "order by p.id desc")
    List<Post> findPostsByAuthorIds(@Param("authorIds") List<Long> authorIds, @Param("startPostId") long startPostId,
                                    Pageable pageable);
}
