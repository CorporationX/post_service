package faang.school.postservice.dto.like;

import faang.school.postservice.validator.dto.DtoValidationConstraints;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeDto {

  private Long id;
  @NotNull(message = DtoValidationConstraints.LIKE_DTO_USER_ID_MISSING)
  private long userId;
  private Long commentId;
  private Long postId;
  private LocalDateTime createdAt;

}
