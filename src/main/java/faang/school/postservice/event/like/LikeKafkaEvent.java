package faang.school.postservice.event.like;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LikeKafkaEvent {
    private Long postId;
    private Long userId;
    private boolean isIncrement;
}
