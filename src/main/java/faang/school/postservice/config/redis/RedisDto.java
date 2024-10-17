package faang.school.postservice.config.redis;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Data
@PropertySource("application.yaml")
public class RedisDto {
    @Value("${spring.data.redis.port}") Integer port;
    @Value("${spring.data.redis.host}") String host;
}
