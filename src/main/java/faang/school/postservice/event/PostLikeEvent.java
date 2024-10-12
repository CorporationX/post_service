package faang.school.postservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostLikeEvent {
    private long postId;
    private long postAuthorId;
    private long likeAuthorId;
}