package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

public class LikeMapperTest {
    private LikeMapper likeMapper;

    @BeforeEach
    void setUp() {
        likeMapper = Mappers.getMapper(LikeMapper.class);
    }

    @Test
    void testToDto() {
        Like like = Like.builder()
                .id(1L)
                .userId(2L)
                .post(Post.builder().id(3L).build())
                .createdAt(LocalDateTime.of(2024, 10, 4, 12, 34, 56))
                .build();


        LikeResponseDto actualDto = likeMapper.toDto(like);
        LikeResponseDto expectedDto = new LikeResponseDto();
        expectedDto.setId(like.getId());
        expectedDto.setUserId(like.getUserId());
        expectedDto.setPostId(like.getPost().getId());
        expectedDto.setCreatedAt(like.getCreatedAt());

        Assertions.assertThat(expectedDto)
                .usingRecursiveComparison()
                .isEqualTo(actualDto);
    }

}
