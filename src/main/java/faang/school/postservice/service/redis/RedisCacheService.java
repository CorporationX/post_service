package faang.school.postservice.service.redis;

public interface RedisCacheService<E, ID> {

    E save(E entity);

    void delete(ID id);

    E findById(ID id);
}
