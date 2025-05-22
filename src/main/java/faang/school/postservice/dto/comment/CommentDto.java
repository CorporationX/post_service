package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private Long authorId;
    private long postId;
    private LocalDateTime createdAt;
}
