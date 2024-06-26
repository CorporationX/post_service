package faang.school.postservice.dto.redis.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostViewEvent {
    private long postId;
    private long authorId;
    private long userId;
    @Builder.Default
    private LocalDateTime viewedAt = LocalDateTime.now();
}
