package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Min;
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
public class LikeDto {
    @Min(value = 1)
    private Long id;

    @Min(value = 1)
    @NotNull
    private Long userId;

    @Min(value = 1)
    private Long commentId;

    @Min(value = 1)
    private Long postId;

    private LocalDateTime createdAt;
}
