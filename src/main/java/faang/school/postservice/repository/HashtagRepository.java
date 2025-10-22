package faang.school.postservice.repository;

import faang.school.postservice.model.Hashtag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashtagRepository extends CrudRepository<Hashtag, Long> {

    List<Hashtag> findAllByName(String name);

    @Query("SELECT DISTINCT h.postId FROM Hashtag h WHERE h.name = :name")
    List<Long> findDistinctPostIdsByName(@Param("name") String name);
}