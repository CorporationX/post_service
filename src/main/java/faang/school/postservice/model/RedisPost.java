package faang.school.postservice.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@RedisHash("posts")
public class RedisPost {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private LocalDateTime publishedAt;
}
