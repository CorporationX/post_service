package faang.school.postservice.dto.like;

import faang.school.postservice.model.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeEventDto implements Serializable {
    private Long postAuthorId;
    private Long likerId;
    private EventType eventType;
    private LocalDateTime createdAt;
}
