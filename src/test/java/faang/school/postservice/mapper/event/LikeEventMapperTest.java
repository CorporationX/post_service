package faang.school.postservice.mapper.event;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.event.like.LikeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LikeEventMapperTest {

    private final LikeEventMapper likeEventMapper = new LikeEventMapperImpl();

    private LikeEvent likeEvent;
    private LikeDto likeDto;

    @BeforeEach
    void setUp() {
        likeEvent = LikeEvent.builder()
                .likeId(1)
                .postId(1)
                .authorId(1)
                .build();

        likeDto = LikeDto.builder()
                .id(1)
                .postId(1L)
                .userId(1L)
                .build();
    }

    @Test
    void testToLikeEvent() {
        LikeEvent result = likeEventMapper.toLikeEvent(likeDto);

        assertEquals(likeEvent.getLikeId(), result.getLikeId());
        assertEquals(likeEvent.getPostId(), result.getPostId());
        assertEquals(likeEvent.getAuthorId(), result.getAuthorId());
    }
}