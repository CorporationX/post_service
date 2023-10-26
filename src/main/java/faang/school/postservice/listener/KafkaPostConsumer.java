package faang.school.postservice.listener;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.kafka.KafkaPostEvent;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostConsumer {
    private final PostService postService;
    private final PostMapper postMapper;

    @KafkaListener(topics = "${spring.data.kafka.topics.post}", groupId = "post-group")
    public void listenerPostEvent(KafkaPostEvent kafkaPostEvent, Acknowledgment acknowledgment) {
        PostDto postDto = postService.getPost(kafkaPostEvent.getPostId());
        Post post = postMapper.toEntity(postDto);
        postService.savePostToRedis(post);
        acknowledgment.acknowledge();
    }
}
