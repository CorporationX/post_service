package faang.school.postservice.repository;

import faang.school.postservice.dto.post.PostDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCacheRepository extends CrudRepository<PostDto, Long> {
}
