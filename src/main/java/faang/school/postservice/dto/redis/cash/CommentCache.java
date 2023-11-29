package faang.school.postservice.dto.redis.cash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCache implements Serializable {

    private long id;
    private long authorId;
    private long postId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
