package faang.school.postservice.consumer;

import faang.school.postservice.model.event.Event;
import faang.school.postservice.model.event.PostEvent;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractSimpleConsumer<T extends Event> {
    protected final RedisPostRepository redisPostRepository;

    public void listenEvent(T event, Acknowledgment acknowledgment) {
        boolean updated = false;

        while (!updated) {
            Optional<PostEvent> post = redisPostRepository.findById(event.getPostId());
            if (post.isPresent()) {
                try {
                    processEvent(event, post.get());

                    redisPostRepository.save(post.get());
                    log.debug("Updated post with id {} save in Redis", post.get().getId());
                    updated = true;
                } catch (OptimisticLockingFailureException e) {
                    log.warn("Optimistic lock exception occurred. Retrying...");
                }
            } else {
                updated = true;
            }
        }
        acknowledgment.acknowledge();
    }

    protected abstract void processEvent(T event, PostEvent post);
}
