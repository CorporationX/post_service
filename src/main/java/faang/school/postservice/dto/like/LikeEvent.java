package faang.school.postservice.dto.like;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LikeEvent {
    Long postId;
    Long authorId;
    Long userId;
    LocalDateTime createdAt;
}
