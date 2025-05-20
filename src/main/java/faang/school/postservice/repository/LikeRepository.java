package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    @Transactional
    @Query(value = """
        with deleteRecord as (
            delete from likes l
             where l.user_id = :userId and l.post_id = :postId
            returning *
        )
        select * from deleteRecord
        """,
        nativeQuery = true
    )
    Optional<Like> deleteByPostIdAndUserId(
        @Param("postId") long postId,
        @Param("userId") long userId
    );

    @Transactional
    @Query(value = """
        with deleteRecord as (
            delete from likes l
             where l.user_id = :userId and l.comment_id = :commentId
            returning *
        )
        select * from deleteRecord
        """,
        nativeQuery = true
    )
    Optional<Like> deleteByCommentIdAndUserId(
        @Param("commentId") long commentId,
        @Param("userId") long userId
    );

    Optional<Like> findByPostIdAndUserId(long postId, long userId);

    Optional<Like> findByCommentIdAndUserId(long commentId, long userId);
}
