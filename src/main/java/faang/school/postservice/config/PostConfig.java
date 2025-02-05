package faang.school.postservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class PostConfig {
    @Value("${post.batch-size}")
    private int batchSize;
}
