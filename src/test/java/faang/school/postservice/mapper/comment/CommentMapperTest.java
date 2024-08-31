package faang.school.postservice.mapper.comment;

import static org.assertj.core.api.Assertions.assertThat;

import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class CommentMapperTest {

  private final CommentMapper mapper = Mappers.getMapper(CommentMapper.class);

  public static Comment getComment() {
    return Comment.builder()
        .id(1L)
        .post(Post.builder()
            .id(2L)
            .build())
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
    final var commentEvent = mapper.toEvent(getComment());
    assertThat(commentEvent.postId()).isEqualTo(getCommentEvent().postId());
    assertThat(commentEvent.commentId()).isEqualTo(getCommentEvent().commentId());
  }

}