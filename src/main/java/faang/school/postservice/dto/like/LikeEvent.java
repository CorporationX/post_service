package faang.school.postservice.dto.like;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LikeEvent {
    private long postId;
    private long authorLikeId;
    private long userId;
    private LocalDateTime localDateTime;
}
