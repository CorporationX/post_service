package faang.school.postservice.dto.hashtag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    private long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private String publishedAt;
}
