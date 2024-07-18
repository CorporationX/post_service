package faang.school.postservice.repository;

import faang.school.postservice.dto.post.PostDto;
import org.springframework.data.repository.CrudRepository;

public interface RedisPostRepository extends CrudRepository<PostDto, Long> {
}
