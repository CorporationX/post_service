package faang.school.postservice.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeEventV2 {
    private long likeAuthorId;
    private long likedPostId;
    private long postAuthorId;
    private LocalDateTime likeDateTime = LocalDateTime.now();
}
