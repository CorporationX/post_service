package faang.school.postservice.dto.kafkaevents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeFeedEvent {
    private Long id;
    private Long authorId;
    private Long postId;
}
