package faang.school.postservice.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentEventDto {

    private long commentId;
    private long authorId;
    private long postId;
    private LocalDateTime createdAt;
}
