package faang.school.postservice.config.context.like;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LikeConfig {
    @Bean(name = "chunkSize")
    public Integer chunkSize(@Value("${like.chunk.size:100}") int chunkSize) {
        return chunkSize;
    }
}