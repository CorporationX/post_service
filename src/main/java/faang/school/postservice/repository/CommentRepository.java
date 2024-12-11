package faang.school.postservice.repository;

import faang.school.postservice.model.dto.CommentCount;
import faang.school.postservice.model.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    int countByPostId(long postId);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createdAt DESC")
    List<Comment> findRecentByPostId(long postId, Pageable pageable);

    @Query(value = """
            SELECT sub.id, sub.post_id, sub.content, sub.author_id, sub.created_at
            FROM (
                SELECT c.id, c.post_id, c.content, c.author_id, c.created_at,
                       ROW_NUMBER() OVER (PARTITION BY c.post_id ORDER BY c.created_at DESC) AS rn
                FROM comment c
                WHERE c.post_id IN (:postIds)
            ) sub
            WHERE sub.rn <= 3
            """, nativeQuery = true)
    List<Object[]> findTop3CommentsPerPost(@Param("postIds") List<Long> postIds);

    @Query(value = """
            SELECT sub.post_id, sub.author_id
            FROM (
                SELECT c.post_id, c.author_id, c.created_at,
                       ROW_NUMBER() OVER (PARTITION BY c.post_id ORDER BY c.created_at DESC) AS rn
                FROM comment c
                WHERE c.post_id IN (:postIds)
            ) sub
            WHERE sub.rn <= 3
            """, nativeQuery = true)
    List<Object[]> findTop3CommentsAuthorIdsPerPost(@Param("postIds") List<Long> postIds);

    @Query("SELECT new faang.school.postservice.model.dto.CommentCount(c.post.id, COUNT(c)) " +
            "FROM Comment c " +
            "WHERE c.post.id IN :postIds " +
            "GROUP BY c.post.id")
    List<CommentCount> findCommentCountsByPostIds(@Param("postIds") List<Long> postIds);
}
