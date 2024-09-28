package faang.school.postservice.heater;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentFeedDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Order(5)
@Service
@RequiredArgsConstructor
public class CommentProducer implements Heater{
    private final RedisCache redisCache;
    private final CommentRepository commentRepository;
    private final ObjectMapper objectMapper;
    private final CommentMapper commentMapper;

    @Value("${spring.data.redis.settings.maxSizeComment}")
    private int maxSizeComment;

    @Override
    @Transactional(readOnly = true)
    public synchronized void addInfoToRedis(Long userId, Long postId) {
        List<CommentFeedDto> commentDtoList = commentRepository.findLastLimitComment(postId, maxSizeComment).stream()
                .map(commentMapper::toFeedDto).toList();

        commentDtoList.forEach(commentDto -> {
            try {
                Timestamp updatedTime = commentRepository.getUpdatedTime(commentDto.getId());
                String CommentJson = objectMapper.writeValueAsString(commentDto);
                redisCache.addCommentToCache(postId, CommentJson, updatedTime.getTime());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
