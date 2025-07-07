package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {

    @Value("${spring.data.kafka.topic.comment}")
    private String commentTopic;

    @Value("${spring.data.kafka.topic.userBan}")
    private String userBanTopic;

    @Value("${spring.data.kafka.topic.posts}")
    private String postPublishTopic;

    @Value("${spring.data.kafka.topic.likes}")
    private String postLikeTopic;

    @Value("${spring.data.kafka.topic.heat}")
    private String heatFeedTopic;

    @Value("${spring.data.kafka.standard-partitions-count}")
    private int partitionsCount;


    @Bean
    public NewTopic comment() {
        return TopicBuilder.name(commentTopic).partitions(partitionsCount).build();
    }

    @Bean
    public NewTopic userBan() {
        return TopicBuilder.name(userBanTopic).partitions(partitionsCount).build();
    }

    @Bean
    public NewTopic postPublish() {
        return TopicBuilder.name(postPublishTopic).partitions(partitionsCount).build();
    }

    @Bean
    public NewTopic postLike() {
        return TopicBuilder.name(postLikeTopic).partitions(partitionsCount).build();
    }

    @Bean
    public NewTopic feedHeat() {
        return TopicBuilder.name(heatFeedTopic).partitions(partitionsCount).build();
    }
}
