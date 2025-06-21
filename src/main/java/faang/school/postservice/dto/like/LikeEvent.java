package faang.school.postservice.dto.like;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LikeEvent {
    public Long postId;
    public Long authorId;
    public Long userId;
    public LocalDateTime createdAt;
}
