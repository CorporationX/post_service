package faang.school.postservice.dto.like;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Schema(name = "Like")
public class LikeDto {
    @Schema(name = "Id")
    private long id;
    @Schema(name = "User Id")
    private Long userId;
    @Schema(name = "Comment")
    private Long comment;
    @Schema(name = "Post")
    private Long post;
    @Schema(name = "Created At")
    private LocalDateTime createdAt;
}
