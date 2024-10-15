package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
  private long id;
  private String content;
  private long authorId;
  private long projectId;
  private int likeCount;
  private LocalDateTime scheduledAt;
}
