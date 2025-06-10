package faang.school.postservice.dto.like;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponseDto {
    @Schema(description = "Like ID", example = "1")
    private Long id;
    @Schema(description = "User ID", example = "1")
    private Long userId;
    @Schema(description = "Comment ID", example = "1")
    private Long commentId;
    @Schema(description = "Post ID", example = "1")
    private Long postId;
    @Schema(description = "Date and time when the like was created")
    private LocalDateTime createdAt;
}
