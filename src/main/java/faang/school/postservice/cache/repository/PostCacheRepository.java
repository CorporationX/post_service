package faang.school.postservice.cache.repository;

import faang.school.postservice.cache.entity.PostCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository()
public interface PostCacheRepository extends JpaRepository<PostCache, Long> {
}
