package faang.school.postservice.repository;

import faang.school.postservice.model.entity.Hashtag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashtagRepository extends CrudRepository<Hashtag, Long> {
    public Hashtag findHashtagByContent(String content);
}
