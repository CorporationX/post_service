package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostRedisEvent;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RedisRepository extends CrudRepository<PostRedisEvent, Integer> {
    Post findByPostId(Integer postId);
}
