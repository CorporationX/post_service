package faang.school.postservice.service.feed.heat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentFeedDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(5)
public class AddInfoAboutComment implements HeatFeed {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final RedisCacheService redisCacheService;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.settings.maxSizeComment}")
    private int maxSizeComment;

    @Override
    @Transactional(readOnly = true)
    public synchronized void addInfoToRedis(Long userId, Long postId) {
        List<CommentFeedDto> commentDtoList = commentRepository.findLastLimitComment(postId, maxSizeComment).stream().map(commentMapper::toFeedDto).toList();

        commentDtoList.forEach(commentDto -> {
            try {
                Timestamp updatedTime = commentRepository.getUpdatedTime(commentDto.getId());
                String CommentJson = objectMapper.writeValueAsString(commentDto);
                redisCacheService.addCommentToCache(postId, CommentJson, updatedTime.getTime());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
