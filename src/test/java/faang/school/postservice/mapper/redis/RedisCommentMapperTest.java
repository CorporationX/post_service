package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RedisCommentMapperTest {

    private RedisCommentMapper redisCommentMapper = new RedisCommentMapperImpl();

    private Comment comment;
    private RedisCommentDto redisCommentDto;

    private final LocalDateTime createdAt = LocalDateTime.now().minusMonths(1);

    @BeforeEach
    void setUp() {
        comment = Comment.builder()
                .id(1L)
                .authorId(2L)
                .content("Content")
                .createdAt(createdAt)
                .build();
        redisCommentDto = RedisCommentDto.builder()
                .id(1L)
                .authorId(2L)
                .content("Content")
                .createdAt(createdAt)
                .build();
    }

    @Test
    void toDtoTest() {
        RedisCommentDto result = redisCommentMapper.toDto(comment);
        assertEquals(redisCommentDto, result);
    }

    @Test
    void mapCommentsToRedisCommentDtoTest() {
        List<RedisCommentDto> expected = List.of(redisCommentDto, redisCommentDto, redisCommentDto);
        List<RedisCommentDto> result = redisCommentMapper.mapCommentsToRedisCommentDto(new ArrayList<>(List.of(comment, comment, comment, comment)));

        assertEquals(expected, result);
        assertEquals(3, result.size());
    }
}