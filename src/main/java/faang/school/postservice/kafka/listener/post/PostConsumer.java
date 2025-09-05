package faang.school.postservice.kafka.listener.post;

import faang.school.postservice.cache.post.PostCache;
import faang.school.postservice.cache.user.FeedCache;
import faang.school.postservice.kafka.producer.post.PostProducer;
import faang.school.postservice.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.avro.post.PostCreateEvent;
import school.faang.avro.post.PostCreateFanoutEvent;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @KafkaHandler
    public void onPostCreate(PostCreateEvent event) {
        postCache.set(mapper.toPostDto(event));
        //todo запросы на получение подписчиков
        for (int i = 0; i < FOLLOWERS_GAPE_COUNT; i++) {
            producer.onPostFanoutBatch(
                    PostCreateFanoutEvent.newBuilder()
                            .setId(event.getId())
                            .setFollowerIds(getFollowers(i))
                            .setCreatedAt(event.getCreatedAt())
                            .build()
            );
        }
    }

    @KafkaHandler
    public void onPostCreate(PostCreateFanoutEvent event) {
        feedCache.AddAll(event.getFollowerIds(), event.getId(), event.getCreatedAt());
    }

    //todo заглушка
    List<Long> getFollowers(int p) {
        int from = p * batch + 1;
        List<Long> numbers = IntStream.rangeClosed(from, from + batch * 10)
                .mapToLong(i -> i)
                .boxed()
                .collect(Collectors.toList());

        Collections.shuffle(numbers);

        return numbers.subList(0, batch);
    }
}
