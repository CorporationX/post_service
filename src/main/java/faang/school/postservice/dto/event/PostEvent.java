package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostEvent {
  private EventType eventType;
  private Long postId;
  private Long authorId;
  private List<Long> subscriberIds;

  public static enum EventType {
    CREATE, UPDATE, DELETE, PUBLISHED
  }
}


