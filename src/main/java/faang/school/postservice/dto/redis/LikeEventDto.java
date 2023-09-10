package faang.school.postservice.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeEventDto {
    private long postId;
    private long postAuthor;
    private long commentId;
    private long commentAuthor;
    private long likeAuthorId;
    private String likeAuthorName;
    private LocalDateTime dateTime;
}
