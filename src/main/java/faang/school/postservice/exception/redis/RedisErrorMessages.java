package faang.school.postservice.exception.redis;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RedisErrorMessages {
    public static final String REDIS_TRANSACTION_INTERRUPTED =
            "Redis transaction interrupted, when trying to save post with id: %s";
}
