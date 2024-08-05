package faang.school.postservice.consumer.kafka;

import faang.school.postservice.cache.redis.PostCache;
import faang.school.postservice.dto.event.LikeEvent;
import faang.school.postservice.dto.post.CachedPostDto;
import faang.school.postservice.exception.NonRetryableException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.service.post.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@AllArgsConstructor
public class LikeConsumer {
    private final PostCache postCache;
    private final PostService postService;
    private final PostMapper postMapper;


    @Transactional
    @KafkaListener(topics = "${spring.kafka.topics-name.likes}", containerFactory = "containerFactory")
    public void listenLikeEvent(LikeEvent likeEvent, Acknowledgment ack) {
        long postId = likeEvent.getPostId();
        CachedPostDto postDto = postCache.findById(postId)
                .orElse(postMapper.toCachedPostDto(postService.getPostById(postId)));
        if (postDto == null) {
            log.info("post with id = {} not exist", postId);
            throw new NonRetryableException(String.format("поста с id = %d не сущетсвует ", postId));
        }
        postDto.incrementLikesQuantity();
        log.info("added like to post with id = {}", postId);
        ack.acknowledge();
    }
}