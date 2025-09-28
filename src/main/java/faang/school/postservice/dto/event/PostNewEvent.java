package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class PostNewEvent {
    private final long postId;
    private final long authorId;
    private final long projectId;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime publishedAt;
    @Setter
    private List<Long> followees;
}
