package faang.school.postservice.properties.topics;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("spring.kafka1.topics")
public class KafkaTopics {
    private PublishPost publish_post;
    private Likes likes;
    private Comments comments;
    private PostView post_view;

    @Data
    public static class Comments {
        private String name;
        private Long numPartition;
        private Long replicationFactor;
    }

    @Data
    public static class Likes {
        private String name;
        private Long numPartition;
        private Long replicationFactor;
    }

    @Data
    public static class PublishPost {
        private String name;
        private Long numPartition;
        private Long replicationFactor;
    }

    @Data
    public static class PostView {
        private String name;
        private Long numPartition;
        private Long replicationFactor;
    }
}
