package faang.school.postservice.dto.like;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LikeEventDto {
    private long postId;
    private long authorId;
    private long likerId;
    private LocalDateTime createdAt;
}
