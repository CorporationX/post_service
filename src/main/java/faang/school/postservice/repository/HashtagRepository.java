package faang.school.postservice.repository;

import faang.school.postservice.model.Hashtag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashtagRepository extends CrudRepository<Hashtag, Long> {

    List<Long> findDistinctPostIdByName(String name);
}