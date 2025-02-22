package faang.school.postservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationLikeEvent implements Event {
    private Long postId;
    private Long userId;
    private Long postAuthorId;
    private Long authorId;
}
