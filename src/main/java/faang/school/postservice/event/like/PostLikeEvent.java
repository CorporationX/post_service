package faang.school.postservice.event.like;

import lombok.Data;

@Data
public class PostLikeEvent {

    private long postId;
    private long authorId;
}
