package faang.school.postservice.repository;

import faang.school.postservice.config.PostCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostCacheRepository extends CrudRepository<PostCache, String> {

    Optional<PostCache> findByPostId(Long postId);

    List<PostCache> findByPostIdIn(List<Long> postIds);
}
