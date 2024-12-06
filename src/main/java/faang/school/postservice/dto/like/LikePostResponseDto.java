package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class LikePostResponseDto {

    @NotNull
    private Long authorPostId;

    @NotNull
    private Long likedUserId;

    @NotNull
    private Long postId;

    @NotNull
    private LocalDateTime likeTime;
}
