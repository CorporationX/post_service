package faang.school.postservice.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeEventDto extends EventDto {
    private long authorId;
    private long postId;
    private long likeId;
}
