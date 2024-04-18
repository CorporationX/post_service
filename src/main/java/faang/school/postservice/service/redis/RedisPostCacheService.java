package faang.school.postservice.service.redis;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.mapper.RedisPostMapper;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.redis.RedisUserCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPostCacheService {
    private final RedisPostRepository redisPostRepository;
    private final RedisPostMapper redisPostMapper;
    private final RedisUserCacheService redisUserCacheService;
    private final RedisKeyValueTemplate redisTemplate;
    @Value("${spring.data.redis.cache.ttl.post}")
    private int postTtl;

    public void savePost(PostDto postDto) {
        redisUserCacheService.saveUser(postDto.getAuthorId());

        RedisPost redisPost = redisPostMapper.toEntity(postDto);
        redisPost.setTtl(postTtl);

        redisPostRepository.findById(postDto.getId())
                .ifPresentOrElse(
                        (post) -> redisTemplate.update(redisPost),
                        () -> redisPostRepository.save(redisPost)
                );
    }

    public void deletePostById(long postId) {
        redisPostRepository.deleteById(postId);
    }

    /*public void saveComment(CommentDto commentDto) {
        Long authorId = commentDto.getAuthorId();

        redisUserCacheService.saveUser(authorId);
    }

    public void deleteComment(CommentDto commentDto) {
    }*/
}
