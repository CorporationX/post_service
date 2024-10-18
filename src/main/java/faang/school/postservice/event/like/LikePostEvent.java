package faang.school.postservice.event.like;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@Builder
public class LikePostEvent implements Serializable {
    private Long postAuthorId;
    private Long likeAuthorId;
    private long postId;
}
