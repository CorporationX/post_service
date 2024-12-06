package faang.school.postservice.dto.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaCommentEvent {
    private Long commentId;
    private Long postId;
    private Long authorId;
}
