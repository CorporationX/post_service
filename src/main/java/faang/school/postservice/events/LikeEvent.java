package faang.school.postservice.events;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeEvent extends Event {
    private Long postId;
    private Long authorId;
    private Long userId;
}
