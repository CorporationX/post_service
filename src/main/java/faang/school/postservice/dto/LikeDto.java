package faang.school.postservice.dto;

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
    private long id;
    @NotNull
    private Long userId;
    @NotNull
    private Long commentId;
    @NotNull
    private Long postId;
    private LocalDateTime createdAt;
}
