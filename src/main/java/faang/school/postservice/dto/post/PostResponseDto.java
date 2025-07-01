package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    private long id;
    private String title;
    private String content;
    private Long authorId;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long likeCount;
}
