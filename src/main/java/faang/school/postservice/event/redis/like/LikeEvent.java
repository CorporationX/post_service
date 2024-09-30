package faang.school.postservice.event.redis.like;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeEvent {
    private final UUID eventId = UUID.randomUUID();
    private long authorId;
    private long postId;
    private long likeId;
    private long commentId;
    private final LocalDateTime timeStamp = LocalDateTime.now();
}

