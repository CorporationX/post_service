package faang.school.postservice.messaging.consumers;

import faang.school.postservice.dto.post.PostViewEvent;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Data
@RequiredArgsConstructor
public class PostViewConsumer {
    private final RedisPostRepository repository;

    @KafkaListener(
            topics = "post-view",
            groupId = "group"
    )
    public void listen(PostViewEvent postView) {
        repository.increasePostView(postView.getPostId());
    }
}
