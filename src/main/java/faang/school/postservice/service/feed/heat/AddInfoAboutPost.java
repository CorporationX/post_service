package faang.school.postservice.service.feed.heat;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Order(2)
public class AddInfoAboutPost implements HeatFeed {
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final RedisCacheService redisCacheService;

    @Value("${spring.data.redis.directory.post}")
    private String patternByPost;

    @Override
    @Transactional(readOnly = true)
    public void addInfoToRedis(Long userId, Long postId) {
        List<PostDto> postDtoList = postRepository.findAllPublishedPostByID(postId).stream().map(postMapper::toDto).toList();

        postDtoList.forEach(postDto -> {
            redisCacheService.saveToCache(patternByPost, postDto.getId(), postDto);
        });
    }
}
