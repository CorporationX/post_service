package faang.school.postservice.consumer;

import faang.school.postservice.dto.Post.PostCacheDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.LikeEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisPostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeEventConsumer {

    private final RedisPostRepository redisPostRepository;
    private final PostRepository repository;
    private final PostMapper postMapper;

    @KafkaListener(topics = "like_topic")
    public void likeEvent(LikeEvent event, Acknowledgment acknowledgment) {
        log.info("A like event has been received");
        PostCacheDto postCacheDto = redisPostRepository.findById(event.getPostId()).orElse(null);

        if (postCacheDto != null) {
            log.info("The post {} was found in the redis cache.", postCacheDto);
            postCacheDto.setLikeCount(incrementCount(postCacheDto));
            redisPostRepository.save(postCacheDto);
        } else {
            Post post = repository.findById(event.getPostId())
                    .orElseThrow(() -> new EntityNotFoundException("Post with id {} not found" + event.getPostId()));
            log.info("the post {} was found in the database", post);
            PostCacheDto cacheDto = postMapper.toCacheDto(post);
            cacheDto.setLikeCount(incrementCount(cacheDto));
            redisPostRepository.save(cacheDto);
        }
        acknowledgment.acknowledge();
    }

    private synchronized Long incrementCount(PostCacheDto postCacheDto) {
        return postCacheDto.getLikeCount() + 1;
    }
}
