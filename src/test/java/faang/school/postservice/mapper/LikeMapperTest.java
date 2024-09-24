package faang.school.postservice.mapper;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.model.Like;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LikeMapperTest {
    private static final long RANDOM_ID = 1L;
    private static final long RANDOM_OTHER_ID = 2L;

    private LikeMapperImpl mapper;
    private LikeDto dto;
    private Like like;

    @BeforeEach
    void setUp() {
        //Arrange
        mapper = new LikeMapperImpl();
        dto = new LikeDto();
        dto.setId(RANDOM_ID);
        dto.setUserId(RANDOM_OTHER_ID);
        like = new Like();
        like.setId(RANDOM_ID);
        like.setUserId(RANDOM_OTHER_ID);
    }

    @Test
    void testToDto() {
        //Assert
        assertEquals(dto, mapper.toDto(like));
    }

    @Test
    void testToEntity() {
        //Act
        Like entity = mapper.toEntity(dto);
        //Assert
        assertEquals(like.getId(), entity.getId());
        assertEquals(like.getUserId(), entity.getUserId());
    }
}