package faang.school.postservice.dto.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostViewEvent {
    private Long postId;

    private Long authorId;

    @Setter
    private Long viewerId;

    @Setter
    private LocalDateTime timestamp;
}
