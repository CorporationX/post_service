package faang.school.postservice.consumer.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.cache.PostCache;
import faang.school.postservice.dto.event.PostLikeEventDto;
import faang.school.postservice.dto.post.PostDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LikePostConsumer extends AbstractConsumer<PostLikeEventDto> {

    private final PostCache postCache;

    public LikePostConsumer(PostCache postCache, ObjectMapper objectMapper) {
        super(objectMapper, PostLikeEventDto.class);
        this.postCache = postCache;
    }

    @KafkaListener(topics = "${spring.kafka.topic.like-post}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeEvent(String message, Acknowledgment acknowledgment) {
        PostLikeEventDto postLikeEventDto = convertMessageToObject(message);
        PostDto postDto = postCache.getByKey(postLikeEventDto.getPostId());

        if (postDto != null) {
            postCache.addLike(postDto);
        }

        acknowledgment.acknowledge();
    }
}
