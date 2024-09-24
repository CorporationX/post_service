package faang.school.postservice.mapper;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.event.LikeEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
public class LikeEventMapperTest {

    private LikeEventMapperImpl likeEventMapper;
    private LikeDto likeDto;

    @BeforeEach
    public void setUp() {
        likeEventMapper = new LikeEventMapperImpl();
        likeDto = new LikeDto();
        likeDto.setId(1L);
        likeDto.setUserId(2L);
        likeDto.setCommentId(3L);
        likeDto.setPostId(4L);
    }

    @Test
    public void testToEntity() {
        LikeEvent likeEvent = likeEventMapper.toEntity(likeDto);

        Assertions.assertNotNull(likeEvent);
        Assertions.assertEquals(likeDto.getPostId(), likeEvent.getPostId());
        Assertions.assertEquals(likeDto.getUserId(), likeEvent.getUserId());
        Assertions.assertNotNull(likeEvent.getEventAt());
        Assertions.assertTrue(likeEvent.getEventAt().isBefore(LocalDateTime.now()) ||
                likeEvent.getEventAt().isEqual(LocalDateTime.now()));
    }
}
