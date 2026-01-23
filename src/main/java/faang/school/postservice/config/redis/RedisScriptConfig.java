package faang.school.postservice.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class RedisScriptConfig {

    @Bean
    public DefaultRedisScript<Long>  incrementLikesScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText("""
                local countKey = KEYS[1]
                local userLikesKey = KEYS[2]
                local userid = ARGV[1]
                
                local alreadyLiked = redis.call("SISMEMBER", userLikesKey, userid)
                if alreadyLiked == 1 then
                    local current = redis.call("GET", countKey)
                    return toNumber(current) or 0
                end
                
                redis.call('SADD', userLikesKey, userid)
                local newCount = redis.call('INCR', countKey)
                return newCount
                """);
        script.setResultType(Long.class);
        return script;
    }

    @Bean
    public DefaultRedisScript<Long>  decrementLikesScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText("""
                local countKey = KEYS[1]
                local userLikesKey = KEYS[2]
                local userid = ARGV[1]
                
                local alreadyLiked = redis.call("SISMEMBER", userLikesKey, userid)
                if alreadyLiked == 0 then
                    local current = redis.call("GET", countKey)
                    return toNumber(current) or 0
                end
                
                redis.call('SREM', userLikesKey, userid)
                local newCount = redis.call('DECR', countKey)
                if newCount < 0 then
                    redis.call('SET', countKey, 0)
                    return 0
                end
                return newCount
                """);
        script.setResultType(Long.class);
        return script;
    }
}
