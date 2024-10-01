package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.events.PostViewEvent;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.redis.service.PostCacheService;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer {
    private final PostCacheService postCacheService;
    private final PostRepository postRepository;
    private final PostMapper mapper;

    @KafkaListener(topics = "${spring.kafka.topic-name. post-views:post_views}")
    void listener(PostViewEvent event){
        addPostView(event.postId());
    }

    private void addPostView(Long postId){
        if (postCacheService.existsById(postId)) {
            postCacheService.incrementConcurrentPostViews(postId);
        }else {
            var postDto = postRepository.findById(postId)
                    .map(mapper::toDto)
                    .orElseThrow(() -> new EntityNotFoundException("Post not found"));
            postDto.setViews(1);
            postCacheService.savePostCache(postDto);
        }
    }
}