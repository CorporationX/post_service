package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostViewDto {
    private long postId;
    private long authorId;
    private long userId;
    private LocalDateTime timestamp;
}