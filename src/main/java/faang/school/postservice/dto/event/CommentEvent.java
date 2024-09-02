package faang.school.postservice.dto.event;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CommentEvent(UUID eventId,
                           long postId,
                           long commentId,
                           LocalDateTime receivedAt) {

  public CommentEvent {
    eventId = UUID.randomUUID();
    receivedAt = LocalDateTime.now();
  }

}
