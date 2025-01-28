package faang.school.postservice.listener.kafka;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentEventListener {
    private final CacheService cacheService;
    @KafkaListener(topics = "${spring.kafka.topic-name.comments}", groupId = "1")
    public void listenGroupFoo(CommentEvent comment) {
       cacheService.addNewCommentToPost(comment);
    }
}
