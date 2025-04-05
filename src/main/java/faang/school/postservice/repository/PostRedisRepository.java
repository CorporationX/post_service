package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRedisRepository extends CrudRepository<Post, Long> {
}
