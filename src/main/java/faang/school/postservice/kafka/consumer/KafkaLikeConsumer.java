package faang.school.postservice.kafka.consumer;

import faang.school.postservice.model.event.LikeEvent;
import faang.school.postservice.model.event.PostEvent;
import faang.school.postservice.repository.cache.LikeCacheRepository;
import faang.school.postservice.repository.cache.PostCacheRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class KafkaLikeConsumer {
    PostCacheRepository postCacheRepository;
    LikeCacheRepository likeCacheRepository;

    @KafkaListener(topics = "${post-service.kafka.like-topic}")
    public void listen(@Payload LikeEvent event,
                       @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment ack) {
        ack.acknowledge();
        PostEvent post = postCacheRepository.getPost(event.getPostId());
        if (post == null) {
            return;
        }
        likeCacheRepository.cacheLike(post.getId(), event);
    }
}
