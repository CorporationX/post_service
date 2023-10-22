package faang.school.postservice.messaging.consumers;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Data
@Slf4j
@RequiredArgsConstructor
public class CommentConsumer {
    private final RedisPostRepository postRepository;

    @KafkaListener(
            topics = "comment-publication",
            groupId = "group"
    )
    public void listen(CommentDto commentDto) {
        PostCacheDto postById = postRepository.getPostById(commentDto.getPostId());
        postRepository.addComment(postById.getPostId(), commentDto);
    }
}
