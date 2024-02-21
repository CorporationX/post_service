package faang.school.postservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikePostEvent {
    private Long counter;
    private Long postId;
    private Long authorId;
}
