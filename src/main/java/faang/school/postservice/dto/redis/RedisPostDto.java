package faang.school.postservice.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class RedisPostDto {
    private long id;
    private String content;
    private Long authorId;
    private Long likeCount;
    private Long commentCount;
    private LocalDateTime createdAt;
}
