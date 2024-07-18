package faang.school.postservice.repository;

import faang.school.postservice.model.Hashtag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HashtagRepository extends CrudRepository<Hashtag, Long> {

}
