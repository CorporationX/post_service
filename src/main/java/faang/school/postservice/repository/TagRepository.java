package faang.school.postservice.repository;

import faang.school.postservice.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {
    @Query("SELECT t FROM Tag t where t.name LIKE %:names%")
    List<Tag> findTagByNameLikeIgnoreCase(String names);

    @Query("SELECT t FROM Tag t ORDER BY t.rating LIMIT :mostPopularOf")
    List<Tag> getMostPopularTags(int mostPopularOf);
}
