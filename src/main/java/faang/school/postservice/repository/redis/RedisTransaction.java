package faang.school.postservice.repository.redis;

import io.lettuce.core.api.sync.RedisCommands;

@FunctionalInterface
public interface RedisTransaction {
    void execute(RedisCommands<String, String> commands);
}
