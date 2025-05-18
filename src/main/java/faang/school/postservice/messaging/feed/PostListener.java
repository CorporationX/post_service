package faang.school.postservice.messaging.feed;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.service.PostService;
import faang.school.postservice.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostListener {
    private final PostService postService;
    private final JsonUtils jsonUtils;

    @KafkaListener(
            topics = "${spring.kafka.topics.feed.post-create-topic}",
            groupId = "${spring.kafka.groups.feed-group}"
    )
    public void publishPostListener(String data) {
        log.info("Received data from kafka: {}", data);
        Long postId = Long.valueOf(data);
        postService.publishPostConsumer(postId);
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.feed.post-update-topic}",
            groupId = "${spring.kafka.groups.feed-group}"
    )
    public void updatePostListener(String data) {
        log.info("Received data from kafka: {}", data);
        PostRequestDto postRequestDto = jsonUtils.deserialize(data, PostRequestDto.class);
        postService.updatePostConsumer(postRequestDto);
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.feed.post-delete-topic}",
            groupId = "${spring.kafka.groups.feed-group}"
    )
    public void deletePostListener(String data) {
        log.info("Received data from kafka: {}", data);
        Long postId = Long.valueOf(data);
        postService.deletePostConsumer(postId);
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.feed.post-view-topic}",
            groupId = "${spring.kafka.groups.feed-group}"
    )
    public void viewPostListener(String data) {
        log.info("Received data from kafka: {}", data);
        Long postId = Long.valueOf(data);
        postService.viewPostConsumer(postId);
    }
}
