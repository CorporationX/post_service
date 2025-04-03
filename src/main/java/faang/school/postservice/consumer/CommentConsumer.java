package faang.school.postservice.consumer;

import faang.school.postservice.event.CommentEvent;
import faang.school.postservice.service.NewsFeedService;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentConsumer {
    private final NewsFeedService newsFeedService;
    private final CommentService commentService;

    @KafkaListener(topics = "${spring.kafka.topics.comment.name}")
    private void listen(CommentEvent event) {
        newsFeedService.cacheCommentForPost(
                commentService.getCommentById(event.getCommentId())
        );
    }
}
