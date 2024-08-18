package faang.school.postservice.mapper.comment;

import static org.assertj.core.api.Assertions.assertThat;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.event.CommentEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class CommentMapperTest {

  private final CommentMapper mapper = Mappers.getMapper(CommentMapper.class);

  public static CommentDto getCommentDto() {
    return CommentDto.builder()
        .id(1L)
        .postId(2L)
        .build();
  }

  public static CommentEvent getCommentEvent() {
    return CommentEvent.builder()
        .commentId(1L)
        .postId(2L)
        .build();
  }

  @Test
  @DisplayName("Проверка равенства CommentEvent и CommentDto после маппинга")
  void testConverterMentorshipDtoFromUser() {
    final var commentEvent = mapper.toEvent(getCommentDto());
    assertThat(commentEvent.postId()).isEqualTo(getCommentEvent().postId());
    assertThat(commentEvent.commentId()).isEqualTo(getCommentEvent().commentId());
  }

}