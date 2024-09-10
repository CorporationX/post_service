package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PostLikeEventDto {
    private Long postId;
    private Long authorId;
    private Long actionUserId;
}
