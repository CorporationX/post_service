package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCache {
    private Long id;
    private Long authorId;
    private String content;
    private LocalDateTime createdAt;
}
