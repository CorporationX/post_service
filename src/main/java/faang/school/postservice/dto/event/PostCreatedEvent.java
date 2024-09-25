package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PostCreatedEvent {
    private Long postId;
    private List<Long> followersIds;
}
