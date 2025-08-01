package faang.school.postservice.dto.redis;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RedisPostDto {
    private long id;
    private String content;
    private Long authorId;
    private Long likeCount;
    private Long commentCount;
    private LocalDateTime createdAt;
}
