package faang.school.postservice.mapper;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class LikeMapperTest {

    private final LikeMapper likeMapper = new LikeMapperImpl();

    @Test
    public void testToDtoWhenEntityNotNullThenReturnDtoWithCorrectValues() {
        LocalDateTime createdAt = LocalDateTime.now();
        Like like = Like.builder()
                .id(1L)
                .userId(1L)
                .comment(new Comment())
                .post(new Post())
                .createdAt(createdAt)
                .build();
        like.getComment().setId(1L);
        like.getPost().setId(1L);

        LikeDto likeDto = likeMapper.toDto(like);

        assertThat(likeDto).isNotNull();
        assertThat(likeDto.userId()).isEqualTo(like.getUserId());
        assertThat(likeDto.commentId()).isEqualTo(like.getComment().getId());
        assertThat(likeDto.postId()).isEqualTo(like.getPost().getId());
        assertThat(likeDto.createdAt()).isEqualTo(like.getCreatedAt());
    }

    @Test
    public void testToDtoWhenEntityNullThenReturnNull() {
        LikeDto likeDto = likeMapper.toDto(null);

        assertThat(likeDto).isNull();
    }

    @Test
    public void testToEntityWhenDtoNotNullThenReturnEntityWithCorrectValues() {
        LocalDateTime createdAt = LocalDateTime.now();
        LikeDto likeDto = new LikeDto(1L, 1L, 1L, createdAt);

        Like like = likeMapper.toEntity(likeDto);

        assertThat(like).isNotNull();
        assertThat(like.getUserId()).isEqualTo(likeDto.userId());
        assertThat(like.getCreatedAt()).isEqualTo(likeDto.createdAt());
    }

    @Test
    public void testToEntityWhenDtoNullThenReturnNull() {
        Like like = likeMapper.toEntity(null);

        assertThat(like).isNull();
    }
}