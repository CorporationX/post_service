package faang.school.postservice.config;

import faang.school.postservice.dto.post.PostDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class PostScheduleConfig {
    @Bean
    public ConcurrentHashMap<LocalDateTime, Set<PostDto>> postMap() {
        return new ConcurrentHashMap<LocalDateTime, Set<PostDto>>();
    }
}
