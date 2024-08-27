package faang.school.postservice.mapper.comment.like;

import static org.assertj.core.api.Assertions.assertThat;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import java.time.LocalDateTime;
import java.time.Month;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class LikeMapperTest {

  private final LikeMapper mapper = Mappers.getMapper(LikeMapper.class);

  @Test
  void testToEntity() {
    final var likeDto = mapper.toDto(getLike());
    assertThat(likeDto).isEqualTo(getLikeDto());
  }

  @Test
  void testHandlesNullRequest() {
    LikeDto event = mapper.toDto(null);
    assertThat(event).isNull();
  }

  private static Like getLike() {
    return Like.builder()
        .id(1)
        .userId(1L)
        .comment(Comment.builder()
            .id(1L)
            .build())
        .post(Post.builder()
            .id(1L)
            .build())
        .createdAt(LocalDateTime.of(2024, Month.AUGUST, 24,0, 0))
        .build();
  }

  private static LikeDto getLikeDto() {
    return LikeDto.builder()
        .id(1L)
        .userId(1L)
        .commentId(1L)
        .postId(1L)
        .createdAt(LocalDateTime.of(2024, Month.AUGUST, 24,0, 0))
        .build();
  }

}