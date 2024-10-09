package faang.school.postservice.config.kafka.consumer;


import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.service.redis.PostRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostConsumer {
    private final PostRedisService postRedisService;
    private final PostMapper postMapper;

    @KafkaListener(topics = "post_theme", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(PostDto event,
                       Acknowledgment acknowledgment) {
        log.info(event.toString());
        postRedisService.savePost(postMapper.toRedisEntity(event));
        acknowledgment.acknowledge();
    }

}
