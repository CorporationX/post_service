package faang.school.postservice.service;

public interface RedisService {

    void pushToRedisUsersForBan(Long authorId);
}
