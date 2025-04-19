package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeEvent {
    private long likeId;
    private long authorId;
    private long postId;

}
