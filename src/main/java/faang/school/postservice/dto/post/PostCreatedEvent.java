package faang.school.postservice.dto.post;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostCreatedEvent {
    private long id;
    private String content;
    private long projectId;
    private long authorId;
}
