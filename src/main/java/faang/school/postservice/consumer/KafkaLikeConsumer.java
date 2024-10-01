package faang.school.postservice.consumer;

import faang.school.postservice.dto.post.CachedPostDto;
import faang.school.postservice.dto.publishable.LikeEvent;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.post.PostService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
@Slf4j
public class KafkaLikeConsumer {
    private final PostService postService;
    private final RedisPostRepository redisPostRepository;


    @Transactional
    @KafkaListener(topics = "${spring.kafka.topic-name.likes}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "containerFactory")
    public void listenLikeEvent(LikeEvent likeEvent, Acknowledgment ack) {
        log.info("event {} received", likeEvent);
        long postId = likeEvent.getPostId();
        CachedPostDto postDto = postService.getPostFromCache(postId);
        if (postDto == null){
            log.info("post with id = {} not exist", postId);
            throw new IllegalArgumentException(String.format("post with id = %d not exist", postId));
        }
        postDto.incrementLikesCount();
        redisPostRepository.save(postDto.getId(),postDto);
        log.info("added like to post with id = {}", postId);
        ack.acknowledge();
    }
}
