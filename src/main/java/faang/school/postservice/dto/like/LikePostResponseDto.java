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

    @NotNull(message = "Author post ID cannot be null")
    private Long authorPostId;

    @NotNull(message = "Liked user ID cannot be null")
    private Long likedUserId;

    @NotNull(message = "Post ID cannot be null")
    private Long postId;

    @NotNull(message = "Like time cannot be null")
    private LocalDateTime likeTime;
}
