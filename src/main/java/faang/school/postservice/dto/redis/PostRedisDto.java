package faang.school.postservice.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRedisDto {
    private Long id;
    private Long authorId;
    private String text;
    private Long projectId;
    private Long likeCount;
    private LocalDateTime createdAt;

    @Builder.Default
    private List<CommentRedisDto> comments = new ArrayList<>();
}
