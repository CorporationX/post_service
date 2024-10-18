package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentEvent {
  private Long id;
  private String content;
  private Long authorId;
  private Long postId;
  private LikeEvent.EventType eventType;

  public static enum EventType {
    CREATE, DELETE
  }
}
