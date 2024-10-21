package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);
    @Query("SELECT c from Comment c where c.post.id = :postId ORDER BY c.createdAt DESC")
    List<Comment> findAllByPostIdSorted(long postId);
    List<Comment> findAllByVerifiedDateIsNull();

    @Query(value = "SELECT * FROM ( " +
            "  SELECT c.*, ROW_NUMBER() OVER (PARTITION BY c.post_id ORDER BY c.created_at DESC) as rn " +
            "  FROM comment c " +
            "  WHERE c.post_id IN (:postIds) AND c.deleted = false" +
            ") sub " +
            "WHERE sub.rn <= :limit", nativeQuery = true)
    List<Comment> findLastsByPostId(Set<Long> postIds, int limit);
}
