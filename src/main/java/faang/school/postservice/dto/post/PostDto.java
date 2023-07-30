package faang.school.postservice.dto.post;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostDto {
    private long id;
    private List<String> hashtags;
    private LocalDateTime publishedAt;
}
