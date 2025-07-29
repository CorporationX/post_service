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
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.LinkedList;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer implements MessageConsumer<String> {
    private final PostCacheRepository postCacheRepository;
    private final ObjectMapper objectMapper;

    @Value("${entity.post.max_cached_comments}")
    private int maxCachedComments;

    @KafkaListener(topics = "${spring.data.kafka.topic_names.comments}")
    public void consume(String json) {
        log.info("Message received. Comment [{}]", json);

        try {
            CommentEvent comment = objectMapper.readValue(json, CommentEvent.class);
            updatePostCommentCache(comment);
        } catch (JsonProcessingException e) {
            log.info("Error on parsing json. Comment [{}]", json);
            throw new RuntimeException();
        }
    }

    private void updatePostCommentCache(CommentEvent comment) {
        if (comment.postId() == null) {
            log.info("Message for comment [{}] wasn't processed cause: empty post id.", comment.commentId());
            return;
        }

        PostCacheDto postCacheDto = postCacheRepository.get(comment.postId());
        if (postCacheDto == null) {
            log.info("Message for comment [{}] wasn't processed cause: post [{}] cache not exists.", comment.commentId(), comment.postId());
            return;
        }

        processComments(comment.commentId(), postCacheDto.getCommentIds());
        postCacheRepository.set(postCacheDto);

        log.info("Message processed successfully. Comment [{}]", comment.commentId());
    }

    private void processComments(long commentId, Deque<Long> currentCommentIds) {
        if (currentCommentIds == null) {
            currentCommentIds = new LinkedList<>();
        }

        if (currentCommentIds.size() > maxCachedComments) {
            currentCommentIds.removeLast();
        }

        currentCommentIds.addFirst(commentId);
    }
}
