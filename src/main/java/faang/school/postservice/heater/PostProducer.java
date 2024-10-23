package faang.school.postservice.heater;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Order(2)
@Service
@RequiredArgsConstructor
public class PostProducer implements Heater{
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final RedisCache redisCache;

    @Value("${spring.data.redis.directory.post}")
    private String patternByPost;

    @Override
    @Transactional(readOnly = true)
    public void addInfoToRedis(Long userId, Long postId) {
        List<PostDto> postDtoList = postRepository.findAllPublishedPostByID(postId).stream()
                .map(postMapper::toDto).toList();

        postDtoList.forEach(postDto -> {
            redisCache.saveToCache(patternByPost, postDto.getId(), postDto);
        });
    }
}
