package faang.school.postservice.dto.post;

import faang.school.postservice.model.post.PostLanguage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    private long id;
    private String content;
    private PostLanguage language;
    private Long authorId;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long likeCount;
}
