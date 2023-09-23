package faang.school.postservice.publisher.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeEvent implements Serializable {
    private Long postId;
    private Long postAuthorId;
    private Long likeAuthorId;
    private LocalDateTime createdAt;
}
