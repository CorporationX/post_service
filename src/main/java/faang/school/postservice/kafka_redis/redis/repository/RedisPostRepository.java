package faang.school.postservice.kafka_redis.redis.repository;

import faang.school.postservice.kafka_redis.redis.model.PostRedisModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPostRepository extends CrudRepository<PostRedisModel, Long> {
}
