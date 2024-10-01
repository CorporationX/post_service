package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.events.PostLikeEvent;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.redis.service.PostCacheService;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {
    private final PostRepository postRepository;
    private final PostCacheService postCacheService;
    private final PostMapper mapper;


    @KafkaListener(topics = "${spring.kafka.topic-name.likes:likes}")
    void listener(PostLikeEvent event){
        incrementLikesInPostCache(event.id());
    }

    private void incrementLikesInPostCache(Long postId){
        if (postCacheService.existsById(postId)){
            postCacheService.incrementConcurrentPostLikes(postId);
        } else {
            var postDto = postRepository.findById(postId)
                    .map(mapper::toDto)
                    .orElseThrow(() -> new EntityNotFoundException("Post not found"));

            postCacheService.savePostCache(postDto);
        }
    }
}