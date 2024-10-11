package faang.school.postservice.repository.cache;


import faang.school.postservice.model.chache.PostCache;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCacheRepository extends CrudRepository<PostCache,Long> {

}
