package faang.school.postservice.listener;

import faang.school.postservice.event.PostCreateEvent;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.feed.RedisFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedPostEventConsumer {

    private final PostService postService;
    private final RedisFeedService redisFeedService;

    @KafkaListener(topics = "post", groupId = "post-group")
    public void listen(PostCreateEvent event, Acknowledgment ack) {
        //тут нужна логика добавления в фид данных
        ack.acknowledge();
    }
}
