package faang.school.postservice.kafka.listener.post;

import faang.school.postservice.cache.post.PostCache;
import faang.school.postservice.cache.user.FeedCache;
import faang.school.postservice.kafka.producer.post.PostProducer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.repository.FollowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.avro.post.PostCreateEvent;
import school.faang.avro.post.PostCreateFanoutEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

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
    @Qualifier("postCreateProducer")
    private final Executor executor;

    @KafkaHandler
    public void onPostCreate(PostCreateEvent event) {
        postCache.set(mapper.toPostDto(event));

        long total = followerRepository.countByAuthorId(event.getAuthorId());
        long totalPages = (total + batch - 1) / batch;
        for (int page = 0; page < totalPages; page++) {
            int finalPage = page;
            CompletableFuture.runAsync(() -> {
                List<Long> content = followerRepository.findAllAuthorFollowerIds(
                        event.getAuthorId(),
                        PageRequest.of(finalPage, batch)
                ).getContent();

                if (!content.isEmpty()) {
                    producer.onPostFanoutBatch(
                            PostCreateFanoutEvent.newBuilder()
                                    .setId(event.getId())
                                    .setFollowerIds(content)
                                    .setCreatedAt(event.getCreatedAt())
                                    .build()
                    );

                }
            }, executor);
        }
    }

    @KafkaHandler
    public void onPostCreate(PostCreateFanoutEvent event) {
        feedCache.addAll(event.getFollowerIds(), event.getId(), event.getCreatedAt());
    }
}
