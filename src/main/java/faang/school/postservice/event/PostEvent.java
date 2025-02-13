package faang.school.postservice.event;

import java.util.List;
import java.util.UUID;

public record PostEvent(UUID eventId,
                        Long postId,
                        Long authorId,
                        List<Long> followerIds) {
    public static PostEvent create(Long postId, Long authorId, List<Long> followerIds) {
        return new PostEvent(UUID.randomUUID(), postId, authorId, followerIds);
    }
}
