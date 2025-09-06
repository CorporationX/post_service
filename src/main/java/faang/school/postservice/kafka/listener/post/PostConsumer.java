package faang.school.postservice.kafka.listener.post;

import faang.school.postservice.cache.post.PostCache;
import faang.school.postservice.cache.user.FeedCache;
import faang.school.postservice.kafka.producer.post.PostProducer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.repository.FollowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.avro.post.PostCreateEvent;
import school.faang.avro.post.PostCreateFanoutEvent;

import java.util.List;

@RequiredArgsConstructor
@KafkaListener(topics = "${spring.kafka.topics.post.name}")
@Component
public class PostConsumer {
    private static final int FOLLOWERS_GAPE_COUNT = 4;
    @Value("${spring.kafka.topics.post.batch}")
    private int batch;

    private final PostCache postCache;
    private final FeedCache feedCache;
    private final PostMapper mapper;
    private final PostProducer producer;
    private final FollowerRepository followerRepository;

    @KafkaHandler
    public void onPostCreate(PostCreateEvent event) {
        postCache.set(mapper.toPostDto(event));
        int page = 0;
        while (true) {
            List<Long> followerIds = followerRepository.findAllAuthorFollowerIds(
                    event.getAuthorId(),
                    PageRequest.of(page, batch)
            );
            if (followerIds.isEmpty()) {
                break;
            }
            producer.onPostFanoutBatch(
                    PostCreateFanoutEvent.newBuilder()
                            .setId(event.getId())
                            .setFollowerIds(followerIds)
                            .setCreatedAt(event.getCreatedAt())
                            .build()
            );
            page++;
        }
    }

    @KafkaHandler
    public void onPostCreate(PostCreateFanoutEvent event) {
        feedCache.addAll(event.getFollowerIds(), event.getId(), event.getCreatedAt());
    }
}
