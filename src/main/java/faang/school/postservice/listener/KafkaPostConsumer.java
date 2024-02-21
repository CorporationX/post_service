package faang.school.postservice.listener;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.kafka.CreatePostEvent;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostConsumer {
    private final PostService postService;
    private final PostMapper postMapper;

    @KafkaListener(topics = "${spring.kafka.topics.post-topic}", groupId = "${spring.kafka.client-id}")
    public void listenerPostEvent(CreatePostEvent createPostEvent, Acknowledgment acknowledgment) {
        log.info("Received post event by id: {}", createPostEvent.getPostId());
        PostDto postDto = postService.getPost(Objects.requireNonNull(createPostEvent.getPostId()));
        Post post = postMapper.toEntity(postDto);
        postService.savePostToRedis(post);
        acknowledgment.acknowledge();
    }
}
