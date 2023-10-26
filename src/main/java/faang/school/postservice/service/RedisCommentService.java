package faang.school.postservice.service;

import faang.school.postservice.dto.redis.CommentRedisDto;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.messaging.kafka.events.CommentEvent;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisCommentService {

    private final RedisPostRepository redisPostRepository;
    private final CommentMapper commentMapper;
    private final RedisTemplate<Long, Object> redisCacheTemplate;

    @Value("${spring.data.redis.comments_size}")
    private int commentsSize;

    public void addCommentToPost(CommentEvent commentEvent) {
        while (true) {
            redisCacheTemplate.watch(commentEvent.getPostId());
            Optional<PostRedisDto> post = redisPostRepository.findById(commentEvent.getPostId());
            CommentRedisDto comment = commentMapper.toRedisDto(commentEvent);

            redisCacheTemplate.multi();
            if (post.isPresent()) {
                LinkedHashSet<CommentRedisDto> comments = post.get().getComments();
                if (comments.size() >= commentsSize) {
                    comments.remove(comments.iterator().next());
                }
                post.get().getComments().add(comment);
            }
            redisPostRepository.save(post.get());

            List<Object> result = redisCacheTemplate.exec();
            if (result != null) {
                break;
            }
        }
    }
}
