package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PostViewEvent {
    private long postId;
    private long authorId;
    private long userId;
    @Builder.Default
    private LocalDateTime viewedAt = LocalDateTime.now();
}
