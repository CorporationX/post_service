package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.redis.LikeEventDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LikeEventMapperTest {

    private LikeEventMapper likeEventMapper = new LikeEventMapperImpl();

    private Like like;
    private LikeEventDto likeEventDto;

    private final Long userId = 1L;
    private final Long receiverId = 2L;
    private final LocalDateTime createdAt = LocalDateTime.now().minusMonths(1);

    @BeforeEach
    void setUp() {
        like = Like.builder()
                .userId(userId)
                .post(Post.builder().id(1L).build())
                .createdAt(createdAt)
                .build();
        likeEventDto = LikeEventDto.builder()
                .actorId(userId)
                .receiverId(1L)
                .receivedAt(createdAt)
                .build();
    }

    @Test
    void toDtoTest() {
        LikeEventDto result = likeEventMapper.toDto(like);
        assertEquals(likeEventDto, result);
    }

    @Test
    void toModelTest() {
        Like expected = Like.builder()
                .userId(userId)
                .createdAt(createdAt)
                .build();

        Like result = likeEventMapper.toModel(likeEventDto);

        assertEquals(expected, result);
    }
}