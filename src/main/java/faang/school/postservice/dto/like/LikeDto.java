package faang.school.postservice.dto.like;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LikeDto {

    private Long id;
    private Long userId;
    private Long commentId;
    private Long postId;
    private LocalDateTime createdAt;
}
