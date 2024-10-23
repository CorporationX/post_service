package faang.school.postservice.dto.event;

import faang.school.postservice.model.EventType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@ToString
@Builder
public class LikePostEvent implements Serializable {
    private Long postAuthorId;
    private Long likeAuthorId;
    private long postId;
    private EventType eventType;
    private LocalDateTime createdAt;
}
