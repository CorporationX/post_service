package faang.school.postservice.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeEvent {
    @NotNull
    private Long authorPostId;
    @NotNull
    private Long authorLikeId;
    @NotNull
    private Long postId;
}
