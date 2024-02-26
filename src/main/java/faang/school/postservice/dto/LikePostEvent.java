package faang.school.postservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class LikePostEvent {
    private Long id;
    private Long userId;
    private Long postId;
    private LocalDateTime createdAt;
}