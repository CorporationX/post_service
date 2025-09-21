package faang.school.postservice.repository.redis.user;

import faang.school.postservice.model.redis.UserCache;

public interface UserCacheRepositoryCustom {
    boolean saveIfAbsent(UserCache userCache);
}
