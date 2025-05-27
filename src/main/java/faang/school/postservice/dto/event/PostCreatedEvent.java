package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreatedEvent {
    private Long postId;
    private String text;
    private Long authorId;
    private Long projectId;
    private Long timestamp;
    private List<Long> subscriberIds;
}
