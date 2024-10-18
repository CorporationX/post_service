package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeDto {
    private Long id;
    @NotNull
    private Long userId;
    private Long commentId;
    private Long postId;
}
