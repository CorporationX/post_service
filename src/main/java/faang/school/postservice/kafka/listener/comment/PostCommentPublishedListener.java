package faang.school.postservice.kafka.listener.comment;

import faang.school.postservice.cache.comment.PostCommentCache;
import faang.school.postservice.mapper.comment.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.avro.post.CommentEvent;

@RequiredArgsConstructor
@Component
public class PostCommentPublishedListener {
    private final PostCommentCache cache;
    private final CommentMapper mapper;

    @KafkaListener(topics = "${spring.kafka.topics.post-comment-publish.name}")
    public void listen(CommentEvent event) {
        cache.add(mapper.toCommentDto(event));
    }
}