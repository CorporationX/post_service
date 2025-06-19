package faang.school.postservice.config;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostAndFollowersDto;
import faang.school.postservice.dto.post.PostDto;
import lombok.Getter;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Getter
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    String bootstrapServers;
    private final String postLikeTopicName = "PostLike";
    private final String commentLikeTopicName = "CommentLike";
    private final String postCreationTopicName = "PostCreation";
    private final String postAndFollowersTopicName = "PostAndFollowers";

    @Bean
    public ProducerFactory<String, LikeDto> producerFactory() {
        Map<String, Object> config = new HashMap();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ProducerFactory<String, PostDto> producerFactoryPostDto() {
        Map<String, Object> config = new HashMap();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ProducerFactory<String, PostAndFollowersDto> producerFactoryPostAndFollowersDto() {
        Map<String, Object> config = new HashMap();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public NewTopic taskTopicPostLike() {
        return TopicBuilder.name(postLikeTopicName)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic taskTopicCommentLike() {
        return TopicBuilder.name(commentLikeTopicName)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic taskTopicPostCreation() {
        return TopicBuilder.name(postCreationTopicName)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic taskTopicPostAndFollowersDto() {
        return TopicBuilder.name(postAndFollowersTopicName)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public KafkaTemplate<String, LikeDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, PostDto> kafkaTemplatePostDto() {
        return new KafkaTemplate<>(producerFactoryPostDto());
    }

    @Bean
    public KafkaTemplate<String, PostAndFollowersDto> kafkaTemplatePostAnDFollowersDto() {
        return new KafkaTemplate<>(producerFactoryPostAndFollowersDto());
    }
}
