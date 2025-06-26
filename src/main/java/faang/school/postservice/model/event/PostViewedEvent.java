package faang.school.postservice.model.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PostViewedEvent {
    private Long postId;
    private LocalDateTime viewedAt;
}
