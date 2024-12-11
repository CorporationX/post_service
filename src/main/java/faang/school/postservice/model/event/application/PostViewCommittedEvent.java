package faang.school.postservice.model.event.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostViewCommittedEvent {
    private Long postId;
    private Long postAuthorId;
    private Long viewerId;
}
