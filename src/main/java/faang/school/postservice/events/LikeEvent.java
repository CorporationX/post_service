package faang.school.postservice.events;

import lombok.Data;

@Data
public class LikeEvent {
    private long likeId;
    private long userId;
    private long postId;
    private long commentId;
}
