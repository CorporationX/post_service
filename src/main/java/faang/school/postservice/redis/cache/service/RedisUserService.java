package faang.school.postservice.redis.cache.service;

import faang.school.postservice.redis.cache.model.RedisUser;

public interface RedisUserService {
    void saveUser(RedisUser user);

    RedisUser findbyId(Long id);
}
