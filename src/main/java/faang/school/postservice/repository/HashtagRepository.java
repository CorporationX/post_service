package faang.school.postservice.repository;

import faang.school.postservice.model.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    @Query("SELECT h FROM Hashtag h WHERE h.tag = :hashtag")
    Optional<Hashtag> findByHashtag(String hashtag);

    @Modifying
    @Query(value = "DELETE FROM post_hashtag WHERE post_id = :postId AND hashtag_id = :hashtagId", nativeQuery = true)
    void deletePostHashtag(Long postId, Long hashtagId);
}
