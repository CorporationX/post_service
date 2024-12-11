package faang.school.postservice.model.event.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeCommitedEvent {
    private Long likeId;
    private Long likeAuthorId;
    private Long postId;
    private Long postAuthorId;
}
