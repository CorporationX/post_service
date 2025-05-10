package faang.school.postservice.messaging;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.comment.CommentServiceImpl;
import faang.school.postservice.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentListener {
    private final CommentServiceImpl commentService;
    private final JsonUtils jsonUtils;

    @KafkaListener(
            topics = "${spring.kafka.topics.feed.comment-create-topic}",
            groupId = "${spring.kafka.groups.feed-group}"
    )
    public void createCommentListener(String data) {
        log.info("Received data fron kafka: {}", data);
        CommentRequestDto commentRequestDto = jsonUtils.deserialize(data, CommentRequestDto.class);
        commentService.createCommentConsumer(commentRequestDto);
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.feed.comment-update-topic}",
            groupId = "${spring.kafka.groups.feed-group}"
    )
    public void updateCommentListener(String data) {
        log.info("Received data fron kafka: {}", data);
        CommentUpdateDto commentUpdateDto = jsonUtils.deserialize(data, CommentUpdateDto.class);
        commentService.updateCommentConsumer(commentUpdateDto);
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.feed.comment-delete-topic}",
            groupId = "${spring.kafka.groups.feed-group}"
    )
    public void deleteCommentListener(String data) {
        log.info("Received data fron kafka: {}", data);
        Long commentId = Long.valueOf(data);
        commentService.deleteCommentConsumer(commentId);
    }


}
