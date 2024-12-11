package faang.school.postservice.message.event;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class PostLikeEvent{
    private final Long receiverId;
    private final Long authorId;
    private final String authorName;
    private final Long postId;
    private final LocalDateTime likeTime;
}
