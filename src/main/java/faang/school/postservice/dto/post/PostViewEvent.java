package faang.school.postservice.dto.post;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class PostViewEvent {
    private final Long postId;
    private final Long userAuthorId;
    private final Long projectAuthorId;
    private final Long viewerId;
    private LocalDateTime time = LocalDateTime.now();
}
