package faang.school.postservice.consumer.like;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.consumer.MessageConsumer;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.repository.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer implements MessageConsumer<String> {
    private final PostCacheRepository postCacheRepository;
    private final ObjectMapper objectMapper;

    private final ReentrantLock lock = new ReentrantLock();

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topic_names.likes}")
    public void consume(String event, @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment ack) {
        log.info("Message for liked post [{}] received.", event);

        try {
            LikeEvent likeEvent = objectMapper.readValue(event, LikeEvent.class);
            updatePostLikeCache(likeEvent.postId(), likeEvent.userId());
            ack.acknowledge();

            log.info("Message for liked post [{}], user [{}] processed successfully.", likeEvent.postId(), likeEvent.userId());
        } catch (JsonProcessingException e) {
            log.info("Error on json parsing for published like [{}].", event, e);
        }
    }

    private void updatePostLikeCache(long postId, long userId) {
        lock.lock();
        try {
            Optional<PostCacheDto> cache = postCacheRepository.get(postId);
            if (cache.isEmpty()) {
                log.info("Message for liked post [{}] on user [{}] wasn't processed cause: post cache not exists.", postId, userId);
                return;
            }

            PostCacheDto postCache = cache.get();
            postCacheRepository.set(preparePostCache(postCache, userId));
        } finally {
            lock.unlock();
        }
    }

    private PostCacheDto preparePostCache(PostCacheDto post, long userId) {
        List<Long> likeIds = post.getLikeIds();
        if (likeIds.contains(userId)) {
            return post;
        }

        likeIds.add(userId);
        post.setLikeIds(likeIds);
        post.setLikeCount(post.getLikeCount() + 1);

        return post;
    }
}
