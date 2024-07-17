package faang.school.postservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PostCreatedEvent {
    private Long postId;
    private List<Long> authorFollowerIds;
}
