package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {

	List<Post> findByAuthorId(long authorId);

	List<Post> findByProjectId(long projectId);

	@Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
	List<Post> findByProjectIdWithLikes(long projectId);

	@Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
	List<Post> findByAuthorIdWithLikes(long authorId);

	@Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
	List<Post> findReadyToPublish();

	@Query("SELECT p FROM Post p WHERE p.verified = false")
	Page<Post> findRejected(Pageable pageable);

	@Query("SELECT p FROM Post p WHERE p.verified IS NULL")
	Page<Post> findUnmoderated(Pageable pageable);

	@Query(nativeQuery = true, value = """
		SELECT p FROM Post p
		WHERE p.author_id = ANY(:authorIds) AND p.published = true AND p.deleted = false
		ORDER BY p.published_at DESC
		LIMIT :limit
		""")
	List<Post> findPublishedPostsByAuthors(@Param("authorIds") List<Long> authorIds,
	                                       @Param("limit") int limit);
}