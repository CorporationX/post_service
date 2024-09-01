package faang.school.postservice.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostEvent {

    private Long authorId;
    private Long postId;
}
