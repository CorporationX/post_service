package faang.school.postservice.dto.redisEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeEvent {
    private Long postId;
    private Long authorId;
    private Long userId;
    private LocalDateTime eventAt;
}
