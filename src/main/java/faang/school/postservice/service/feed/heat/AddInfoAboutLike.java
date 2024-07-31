package faang.school.postservice.service.feed.heat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Order(4)
public class AddInfoAboutLike implements HeatFeed {
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final RedisCacheService redisCacheService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public synchronized void addInfoToRedis(Long userId, Long postId) {
        List<LikeDto> likeDtoList = likeRepository.findByPostId(postId).stream().map(likeMapper::toDto).toList();

        likeDtoList.forEach(likeDto -> {
            try {
                String likeJson = objectMapper.writeValueAsString(likeDto);
                redisCacheService.addLikeToCache(postId, likeJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
