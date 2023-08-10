package faang.school.postservice.dto.post;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostDto implements Serializable {
    private long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<String> hashtags;
    private LocalDateTime publishedAt;
}
