package faang.school.postservice.dto.like;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Schema(name = "Like")
@Data
@Builder
public class LikeDto {
    @Schema(name = "Id")
    private long id;
    @Schema(name = "User Id")
    private Long userId;
    @Schema(name = "Comment")
    private Long commentId;
    @Schema(name = "Post")
    private Long postId;
    @Schema(name = "Created At")
    private LocalDateTime createdAt;
}