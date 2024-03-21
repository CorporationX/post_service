package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeEvent {
    @NotNull
    private Long userId;
    @NotNull
    private Long authorPostId;
    @NotNull
    private Long postId;
    private LocalDateTime createdAt;
}
