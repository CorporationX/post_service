package faang.school.postservice.repository.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.CommentFeedDto;
import faang.school.postservice.dto.redis.UserRedisDto;
import faang.school.postservice.dto.user.UserViewDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Redis репозиторий для сущности комментарий.
 * Используется для построения feed пользователя
 *
 * @author Linempy
 * @since 15.11.2025
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CommentRepository commentRepository;
    private final UserServiceClient client;
    private final CommentMapper mapper;
    private final UserMapper userMapper;
    private final UserContext userContext;
    private final ObjectMapper objectMapper;
    private static final String KEY = "comment:";

    @Value("${redis.schema.comment.ttl-day}")
    private int ttlDay;

    public void saveLatestComment(CommentFeedDto dto) {
        String key = getFormattedKey(dto.id());
        redisTemplate.opsForValue().setIfAbsent(key, dto, Duration.ofDays(ttlDay));
    }

    public CommentFeedDto getLatestComment(Long id) {
        String key = getFormattedKey(id);

        return Optional.ofNullable((CommentFeedDto) redisTemplate.opsForValue().get(key))
            .orElseGet(() -> {
                    CommentFeedDto commentFeedDto = processGetRedisDtoFromDb(id);
                    saveLatestComment(commentFeedDto);
                    return commentFeedDto;
                }
            );
    }

    public List<CommentFeedDto> getLatestComments(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Long id : ids) {
                String key = getFormattedKey(id);
                connection.stringCommands().get(key.getBytes());
            }
            return null;
        });

        List<CommentFeedDto> foundComments = results.stream()
                .filter(Objects::nonNull)
                .map(this::convertToCommentFeedDto)
                .filter(Objects::nonNull)
                .toList();

        List<Long> foundIds = foundComments.stream()
                .map(CommentFeedDto::id)
                .toList();
        List<Long> missingIds = ids.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!missingIds.isEmpty()) {
            List<CommentFeedDto> missingComments = missingIds.stream()
                    .map(this::processGetRedisDtoFromDb)
                    .toList();

            missingComments.forEach(this::saveLatestComment);

            return Stream.concat(foundComments.stream(), missingComments.stream())
                    .toList();
        }

        return foundComments;
    }

    public CommentFeedDto processGetRedisDtoFromDb(Long id) {
        Comment comment = commentRepository.findByIdOrThrow(id);
        userContext.setUserId(comment.getAuthorId());
        UserViewDto user = client.getUser(comment.getAuthorId());
        UserRedisDto userRedisDto = userMapper.toRedisDto(user);
        return mapper.toFeedDto(comment, userRedisDto);
    }


    private String getFormattedKey(Long id) {
        return KEY + id;
    }

    private CommentFeedDto convertToCommentFeedDto(Object obj) {
        try {
            return objectMapper.convertValue(obj, CommentFeedDto.class);
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка конвертации Redis data в CommentFeedDto: {}", e.getMessage());
            return null;
        }
    }


}