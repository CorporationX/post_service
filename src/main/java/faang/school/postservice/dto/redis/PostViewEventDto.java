package faang.school.postservice.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostViewEventDto {
    private Long userId;
    private Long postId;
    private Long authorId;
    private Long projectId;
    private LocalDateTime createdAt;
}
