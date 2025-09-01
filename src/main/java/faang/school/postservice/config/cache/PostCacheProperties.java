package faang.school.postservice.config.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class PostCacheProperties {

    @Value("${app.cache.post.prefix}")
    private String prefix;

    @Value("${app.cache.post.ttlHours}")
    private long ttlHours;

}
