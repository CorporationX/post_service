package faang.school.postservice.redis.cache.service;

import org.springframework.data.keyvalue.repository.KeyValueRepository;

import java.util.Optional;

public interface RedisOperations {

    <R extends KeyValueRepository<E, ID>, E, ID> Optional<E> findById(R repository, ID id);

    <R extends KeyValueRepository<E, ID>, E, ID> void deleteById(R repository, ID id);

    <R extends KeyValueRepository<E, ID>, E, ID> E updateOrSave(R repository, E entity, ID id);

    <R extends KeyValueRepository<E, ID>, E, ID> void customUpdate(R repository, ID id, Runnable runnable);
}
