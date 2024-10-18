package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
  private Long id;
  private String content;
  private Long authorId;
  private List<Long> likesId;
  private Long likeCount;
  private Long postId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
