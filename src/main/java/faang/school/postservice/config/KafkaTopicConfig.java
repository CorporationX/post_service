package faang.school.postservice.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {
    
    @Value(value = "${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapAddress;
    @Value("${spring.kafka.topics.post.name}")
    private String postTopic;
    @Value("${spring.kafka.topics.like_post.name}")
    private String likePostTopic;
    @Value("${spring.kafka.topics.comment.name}")
    private String commentTopic;
    @Value("${spring.kafka.topics.post_view.name}")
    private String postViewTopic;
    @Value("${spring.kafka.topics.heat_feed.post}")
    private String heatPostTopic;
    @Value("${spring.kafka.topics.heat_feed.comment}")
    private String heatCommentTopic;
    @Value("${spring.kafka.topics.heat_feed.like_post}")
    private String heatLikeTopic;
    @Value("${spring.kafka.topics.heat_feed.post_view}")
    private String heatViewTopic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topicPost() {
         return new NewTopic(postTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic topicLikePost() {
        return new NewTopic(likePostTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic topicComment() {
        return new NewTopic(commentTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic topicPostView() {
        return new NewTopic(postViewTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic topicHeatPost() {
        return new NewTopic(heatPostTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic topicHeatComment() {
        return new NewTopic(heatCommentTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic topicHeatLike() {
        return new NewTopic(heatLikeTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic topicHeatView() {
        return new NewTopic(heatViewTopic, 1, (short) 1);
    }
}