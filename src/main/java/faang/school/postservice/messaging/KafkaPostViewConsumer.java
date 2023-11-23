package faang.school.postservice.messaging;

import faang.school.postservice.dto.post.KafkaPostView;
import faang.school.postservice.dto.post.RedisPostDto;
import faang.school.postservice.repository.PostRedisRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

@Configuration
@RequiredArgsConstructor
public class KafkaPostViewConsumer {
    private final PostService postService;
    private final PostRedisRepository postRedisRepository;

    @KafkaListener(topics = "${spring.data.kafka.topics.post-views.name}", groupId = "${spring.data.kafka.client-id}")
    public void listenerPostView(KafkaPostView kafkaPostView, Acknowledgment acknowledgment){
        updateViewRedis(kafkaPostView.getPostId());
        acknowledgment.acknowledge();
    }
    private void updateViewRedis(Long id){
        RedisPostDto redisPost = postRedisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found in Redis by id: {}" + id));
        redisPost.setViews(redisPost.getViews() + 1);
        postRedisRepository.save(redisPost);
    }
}
