package faang.school.postservice.dto.like;

import faang.school.postservice.model.EventType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class LikeEventDto implements Serializable {
    private Long postAuthorId;
    private Long likerId;
    private EventType eventType;
    private LocalDateTime createdAt;
}
