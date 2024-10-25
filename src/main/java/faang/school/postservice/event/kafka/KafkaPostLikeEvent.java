package faang.school.postservice.event.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaPostLikeEvent implements Serializable {
    private long postId;
    private long postAuthorId;
    private long likeAuthorId;
}