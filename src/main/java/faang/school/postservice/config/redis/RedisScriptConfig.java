package faang.school.postservice.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisScriptConfig {

    @Bean
    public RedisScript<Boolean> addAndTrimZSetScript() {
        Resource scriptSource = new ClassPathResource("lua/add_and_trim_zset.lua");
        return RedisScript.of(scriptSource, Boolean.class);
    }
}
