package faang.school.postservice.config.kafka;

import faang.school.postservice.config.properties.AuthorRequestTopicProperties;
import faang.school.postservice.config.properties.AuthorResponseTopicProperties;
import faang.school.postservice.config.properties.CommentAddedTopicProperties;
import faang.school.postservice.config.properties.FollowersRequestTopicProperties;
import faang.school.postservice.config.properties.HashtagAddingTopicProperties;
import faang.school.postservice.config.properties.HashtagRemovingTopicProperties;
import faang.school.postservice.config.properties.LikeAddedTopicProperties;
import faang.school.postservice.config.properties.LikeRemovedTopicProperties;
import faang.school.postservice.config.properties.PostsTopicProperties;
import faang.school.postservice.config.properties.PostsViewTopicProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicsConfig {

    private final HashtagAddingTopicProperties hashtagAddingTopic;
    private final HashtagRemovingTopicProperties hashtagRemovingTopic;
    private final FollowersRequestTopicProperties followersRequestTopic;
    private final PostsTopicProperties postsTopic;
    private final CommentAddedTopicProperties commentAddedTopic;
    private final LikeAddedTopicProperties likeAddedTopic;
    private final LikeRemovedTopicProperties likeRemovedTopic;
    private final PostsViewTopicProperties postsViewTopic;
    private final AuthorRequestTopicProperties authorRequestTopic;
    private final AuthorResponseTopicProperties authorResponseTopic;

    @Bean
    public NewTopic hashtagAddingTopic() {
        return createTopic(hashtagAddingTopic.name(),
                hashtagAddingTopic.partitions(),
                hashtagAddingTopic.replicas());
    }

    @Bean
    public NewTopic hashtagRemovingTopic() {
        return createTopic(hashtagRemovingTopic.name(),
                hashtagRemovingTopic.partitions(),
                hashtagRemovingTopic.replicas());
    }

    @Bean
    public NewTopic followersRequestTopic() {
        return createTopic(followersRequestTopic.name(),
                followersRequestTopic.partitions(),
                followersRequestTopic.replicas());
    }

    @Bean
    public NewTopic postsTopic() {
        return createTopic(postsTopic.name(),
                postsTopic.partitions(),
                postsTopic.replicas());
    }

    @Bean
    public NewTopic commentAddedTopic() {
        return createTopic(commentAddedTopic.name(),
                commentAddedTopic.partitions(),
                commentAddedTopic.replicas());
    }

    @Bean
    public NewTopic likeAddedTopic() {
        return createTopic(likeAddedTopic.name(),
                likeAddedTopic.partitions(),
                likeAddedTopic.replicas());
    }

    @Bean
    public NewTopic likeRemovedTopic() {
        return createTopic(likeRemovedTopic.name(),
                likeRemovedTopic.partitions(),
                likeRemovedTopic.replicas());
    }

    @Bean
    public NewTopic postsViewTopic() {
        return createTopic(postsViewTopic.name(),
                postsViewTopic.partitions(),
                postsViewTopic.replicas());
    }

    @Bean
    public NewTopic authorRequestTopic() {
        return createTopic(authorRequestTopic.name(),
                authorRequestTopic.partitions(),
                authorRequestTopic.replicas());
    }

    @Bean
    public NewTopic authorResponseTopic() {
        return createTopic(authorResponseTopic.name(),
                authorResponseTopic.partitions(),
                authorResponseTopic.replicas());
    }

    private NewTopic createTopic(String name, int partitions, int replicas) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
