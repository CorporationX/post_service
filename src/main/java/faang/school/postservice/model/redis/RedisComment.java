package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RedisComment {
    private String content;
    private Long authorId;
    private Integer likes;
}
