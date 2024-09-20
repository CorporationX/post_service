package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LikeDto {
    private Long likeId;

    @NotNull
    private Long userId;
    private Long postId;
    private Long commentId;
    private LocalDateTime createdAt;
}
