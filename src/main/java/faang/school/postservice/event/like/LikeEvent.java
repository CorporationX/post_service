package faang.school.postservice.event.like;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeEvent {
    private UUID eventId = UUID.randomUUID();
    private Long postAuthorId;
    private Long likeAuthorId;
    private Long postId;
    private LocalDateTime createdAt = LocalDateTime.now();
}
