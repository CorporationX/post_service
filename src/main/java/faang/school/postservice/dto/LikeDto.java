package faang.school.postservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class LikeDto {
    private Long id;
    private Long userId;
    private Long commentId;
    private Long postId;
    private LocalDateTime createdAt;
}