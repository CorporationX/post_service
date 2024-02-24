package faang.school.postservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LikeDto {
    private Long id;
    private Long userId;
    private Long commentId;
    private Long postId;
    private LocalDateTime createdAt;
}