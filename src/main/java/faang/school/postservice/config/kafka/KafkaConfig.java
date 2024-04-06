package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value("${spring.kafka.topics.post.name}")
    private String topicPost;
    @Value("${spring.kafka.topics.comment.name}")
    private String topicComment;
    @Value("${spring.kafka.topics.like.name}")
    private String topicLike;
    @Value("${spring.kafka.topics.post_view.name}")
    private String topicPostView;
    @Value("${spring.kafka.topics.post.cache}")
    private String topicCachePost;
    @Value("${spring.kafka.topics.comment.cache}")
    private String topicCacheComment;
    @Value("${spring.kafka.topics.like.cache}")
    private String topicCacheLike;


    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topicPost() {
        return new NewTopic(topicPost, 1, (short) 1);
    }

    @Bean
    public NewTopic topicComment() {
        return new NewTopic(topicComment, 1, (short) 1);
    }

    @Bean
    public NewTopic topicLike() {
        return new NewTopic(topicLike, 1, (short) 1);
    }

    @Bean
    public NewTopic topicPostView() {
        return new NewTopic(topicPostView, 1, (short) 1);
    }

    @Bean
    public NewTopic topicCachePost() {
        return new NewTopic(topicCachePost, 1, (short) 1);
    }

    @Bean
    public NewTopic topicCacheComment() {
        return new NewTopic(topicCacheComment, 1, (short) 1);
    }

    @Bean
    public NewTopic topicCacheLike() {
        return new NewTopic(topicCacheLike, 1, (short) 1);
    }

}
