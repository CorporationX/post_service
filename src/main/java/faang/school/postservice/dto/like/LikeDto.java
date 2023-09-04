package faang.school.postservice.dto.like;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeDto {
    private Long id;
    private Long userId;
    private Long postId;
    private Long commentId;
    private LocalDateTime createdAt;
}
