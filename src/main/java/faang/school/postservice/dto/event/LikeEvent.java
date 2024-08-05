package faang.school.postservice.dto.event;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class LikeEvent {
    private Long id;
    private Long postId;
    private Long commentId;
    private Long userId;
}