package faang.school.postservice.redis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisPost implements Serializable {
    private Long id;
    private Long authorId;
    private Long projectId;
    private String content;
}