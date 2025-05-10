package faang.school.postservice.messaging;

import faang.school.postservice.dto.ike.CommentLikeDto;
import faang.school.postservice.dto.ike.PostLikeDto;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeListener {
    private final LikeService likeService;
    private final JsonUtils jsonUtils;

    @KafkaListener(
            topics = "${spring.kafka.topics.feed.post-like-topic}",
            groupId = "${spring.kafka.groups.feed-group}"
    )
    public void likePostListener(String data) {
        log.info("Received data from kafka: {}", data);
        PostLikeDto postLikeDto = jsonUtils.deserialize(data, PostLikeDto.class);
        likeService.likePostConsumer(postLikeDto);
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.feed.post-unlike-topic}",
            groupId = "${spring.kafka.groups.feed-group}"
    )
    public void unlikePostListener(String data) {
        log.info("Received data from kafka: {}", data);
        PostLikeDto postLikeDto = jsonUtils.deserialize(data, PostLikeDto.class);
        likeService.unlikePostConsumer(postLikeDto);
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.feed.comment-like-topic}",
            groupId = "${spring.kafka.groups.feed-group}"
    )
    public void likeCommentListener(String data) {
        log.info("Received data from kafka: {}", data);
        CommentLikeDto commentLikeDto = jsonUtils.deserialize(data, CommentLikeDto.class);
        likeService.likeCommentConsumer(commentLikeDto);
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.feed.comment-unlike-topic}",
            groupId = "${spring.kafka.groups.feed-group}"
    )
    public void unlikeCommentListener(String data) {
        log.info("Received data from kafka: {}", data);
        CommentLikeDto commentLikeDto = jsonUtils.deserialize(data, CommentLikeDto.class);
        likeService.unlikeCommentConsumer(commentLikeDto);
    }
}