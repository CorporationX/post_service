package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeAddedEvent extends Event {
    private Long postId;
    private Long authorId;
}
