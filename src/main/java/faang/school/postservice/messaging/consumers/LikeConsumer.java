package faang.school.postservice.messaging.consumers;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@Data
@RequiredArgsConstructor
public class LikeConsumer {
    private final RedisPostRepository postRepository;

    @KafkaListener(
            topics = "like-publication",
            groupId = "group"
    )
    public void listen(LikeDto likeDto) {
        PostCacheDto postById = postRepository.getPostById(likeDto.getPostId());
        postRepository.increaseLikeCounter(postById.getPostId());
    }
}
