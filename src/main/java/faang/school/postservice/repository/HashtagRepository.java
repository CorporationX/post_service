package faang.school.postservice.repository;

import faang.school.postservice.model.Hashtag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HashtagRepository extends CrudRepository<Hashtag, Long> {
    public Hashtag findHashtagByContent(String content);

    @Query(value = "INSERT INTO post_hashtag (post_id, hashtag_id) VALUES (:postId, :hashtagId)", nativeQuery = true)
    public void saveHashtagsIntoInnerTable(Long postId, Long hashtagId);
}
