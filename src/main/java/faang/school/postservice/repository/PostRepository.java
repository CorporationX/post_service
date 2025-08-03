package faang.school.postservice.repository;

import faang.school.postservice.dto.post.UserPostsDto;
import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    @Query(
            nativeQuery = true,
            value = "select * from post where author_id in :authorIds order by id desc offset :offset limit :limit"
    )
    List<Post> findPageForAuthors(List<Long> authorIds, int offset, int limit);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query(nativeQuery = true, value = "select * from post order by id asc offset :offset limit :limit")
    List<Post> findPage(long offset, long limit);

    @Query("""
            SELECT new faang.school.postservice.dto.post.UserPostsDto(p.authorId, COUNT(p.id) as count) 
                FROM Post p
                WHERE p.verified = false
                GROUP BY p.authorId 
                HAVING COUNT(p.authorId) > :maxUnverifiedPostsForBan
            """)
    List<UserPostsDto> findUnverifiedPostsCountForUsers(long maxUnverifiedPostsForBan);
}
