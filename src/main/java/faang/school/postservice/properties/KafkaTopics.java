package faang.school.postservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.data.kafka.topics")
public class KafkaTopics {
    private String like;

    private Comment comment;
    private Post post;

    @Data
    public static class Comment {
        private String published;
    }

    @Data
    public static class Post {
        private String published;
        private String views;
    }
}
