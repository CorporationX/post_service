package faang.school.postservice.consumer.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.consumer.MessageConsumer;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.repository.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer implements MessageConsumer<String> {
    private final PostCacheRepository postCacheRepository;
    private final ObjectMapper objectMapper;
    private final ReentrantLock lock = new ReentrantLock();

    @Value("${entity.post.max_cached_comments}")
    private int maxCachedComments;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topic_names.comments}")
    public void consume(String json, @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment ack) {
        log.info("Message received. Comment [{}]", json);

        try {
            CommentEvent comment = objectMapper.readValue(json, CommentEvent.class);
            updatePostCommentCache(comment);
            ack.acknowledge();

            log.info("Message processed successfully. Comment [{}]", comment.commentId());
        } catch (JsonProcessingException e) {
            log.info("Error on parsing json. Comment [{}]", json, e);
        }
    }

    private void updatePostCommentCache(CommentEvent comment) {
        if (comment.postId() == null) {
            log.info("Message for comment [{}] wasn't processed cause: empty post id.", comment.commentId());
            return;
        }

        lock.lock();
        try {
            Optional<PostCacheDto> cache = postCacheRepository.get(comment.postId());
            if (cache.isEmpty()) {
                log.info("Message for comment [{}] wasn't processed cause: post [{}] cache not exists.", comment.commentId(), comment.postId());
                return;
            }

            PostCacheDto postCache = cache.get();
            ConcurrentLinkedDeque<Long> commentIds = processComments(comment.commentId(), postCache.getCommentIds());
            postCache.setCommentIds(commentIds);
            postCacheRepository.set(postCache);
        } finally {
            lock.unlock();
        }
    }

    private ConcurrentLinkedDeque<Long> processComments(long commentId, ConcurrentLinkedDeque<Long> currentCommentIds) {
        ConcurrentLinkedDeque<Long> processedCommentIds = currentCommentIds == null
                ? new ConcurrentLinkedDeque<>()
                : currentCommentIds;
        if (processedCommentIds.isEmpty() || commentId > processedCommentIds.getLast()) {
            processedCommentIds.addFirst(commentId);
        }

        while (processedCommentIds.size() > maxCachedComments) {
            processedCommentIds.removeLast();
        }

        return processedCommentIds;
    }
}
