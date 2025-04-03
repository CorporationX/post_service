package faang.school.postservice.dto.like;

import faang.school.postservice.dto.NotificationEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikePostEvent extends NotificationEvent {
    private long postId;
    private long postAuthorId;
    private long likeUserId;
}
