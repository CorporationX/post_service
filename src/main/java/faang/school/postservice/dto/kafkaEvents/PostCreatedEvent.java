package faang.school.postservice.dto.kafkaEvents;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PostCreatedEvent {
    private Long postId;
    private Long authorId;
    private List<Long> followersIds;
}
