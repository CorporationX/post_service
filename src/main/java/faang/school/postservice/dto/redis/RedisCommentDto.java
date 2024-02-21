package faang.school.postservice.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.sql.In;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisCommentDto {
    private Long id;
    private Long authorId;
    private Long postId;
    private Integer likes;
    private String content;
    private LocalDateTime createdAt;
}
