package faang.school.postservice.dto.kafka;

import faang.school.postservice.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaCommentEvent {
    private Long counter;
    private Long postId;
    private Long authorId;
    private Comment comment;
}
