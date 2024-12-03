package faang.school.postservice.consumer.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.event.comment.CommentCreatedEvent;
import faang.school.postservice.repository.cache.post.PostCacheRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer {

    private final PostCacheRepositoryImpl postCacheRepository;

    @KafkaListener(topics = "${spring.data.kafka.topics.commentCreatedTopic.name}",
            groupId = "${spring.data.kafka.consumerConfig.groupId}")
    public void listenCommentEvent(CommentCreatedEvent event, Acknowledgment acknowledgment) {
        CommentDto commentDto = buildCommentDto(event);
        boolean isProcessed = postCacheRepository.updatePostsComments(event.getPostId(), commentDto);
        log.info("Is processed : {}", isProcessed);
        acknowledgment.acknowledge();
    }

    private CommentDto buildCommentDto(CommentCreatedEvent event) {
        return CommentDto.builder()
                .id(event.getCommentId())
                .authorId(event.getAuthorId())
                .content(event.getContent())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
