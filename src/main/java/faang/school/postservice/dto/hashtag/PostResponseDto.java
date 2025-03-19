package faang.school.postservice.dto.hashtag;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostResponseDto {
    private long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private String publishedAt;
}
