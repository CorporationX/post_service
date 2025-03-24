package faang.school.postservice.repository;

import faang.school.postservice.model.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {
    @Query("SELECT t FROM Tag t where LOWER(t.name) LIKE CONCAT(:tagStartWith, '%')")
    List<Tag> findTagByNameLikeIgnoreCase(@Param("tagStartWith") String tagStartWith, Pageable pageable);

    Optional<Tag> findTagByName(String tagName);
}
