package faang.school.postservice.consumer;

import faang.school.postservice.dto.post.CachedPostDto;
import faang.school.postservice.dto.publishable.PostViewEvent;
import faang.school.postservice.exception.kafka.NonRetryableException;
import faang.school.postservice.service.post.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@AllArgsConstructor
public class KafkaPostViewConsumer {
    private final PostService postService;

    @Transactional
    @KafkaListener(topics = "${spring.kafka.topic-name.post-views}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "containerFactory")
    public void listenPostViewEvent(PostViewEvent postViewEvent, Acknowledgment ack){
        Long postId = postViewEvent.getPostId();
        CachedPostDto postDto = postService.getPostFromCache(postId);
        if(postDto==null){
            log.info("post with id = {} not exist", postId);
            throw  new NonRetryableException(String.format("post with id = %d not exits", postId));
        }
        postDto.incrementViewsCount();
        ack.acknowledge();
    }
}
