package faang.school.postservice.producer;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.properties.PostsTopicProperties;
import faang.school.postservice.config.redis.properties.FeedCacheProperties;
import faang.school.postservice.dto.post.PostEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class KafkaPostProducer extends KafkaAbstractProducer<PostEventDto> {
    private final UserServiceClient userServiceClient;
    private final PostsTopicProperties postsTopicProperties;
    private final FeedCacheProperties feedCacheProperties;

    public KafkaPostProducer(KafkaTemplate<String, PostEventDto> postEventKafkaTemplate,
                             UserServiceClient userServiceClient,
                             PostsTopicProperties postsTopicProperties,
                             FeedCacheProperties feedCacheProperties) {
        super(postEventKafkaTemplate);
        this.userServiceClient = userServiceClient;
        this.postsTopicProperties = postsTopicProperties;
        this.feedCacheProperties = feedCacheProperties;
    }

    public void produce(long postId, LocalDateTime postCreatedAt, long postAuthorId) {
        List<Long> postAuthorFollowersIds = userServiceClient.getFollowersIds(postAuthorId);
        splitFollowersIdsList(postAuthorFollowersIds).forEach(ids ->
                sendMessage(
                        postsTopicProperties.name(),
                        PostEventDto.builder()
                                .postId(postId)
                                .postCreatedAt(postCreatedAt)
                                .postAuthorFollowersIds(ids)
                                .build()
                ));
    }

    private List<List<Long>> splitFollowersIdsList(List<Long> list) {
        int followeesBatch = feedCacheProperties.batch();
        List<List<Long>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += followeesBatch) {
            int end = Math.min(i + followeesBatch, list.size());
            chunks.add(list.subList(i, end));
        }
        return chunks;
    }
}
