package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
@Builder
@RedisHash("Post")
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;

    @NotBlank(message = "Post content can't be blank")
    private String content;

    private Long authorId;

    private Long projectId;

    private List<Long> likesIds;

    private List<Long> commentsIds;

    private boolean published;

    @PastOrPresent(message = "Post can't be published in future")
    private LocalDateTime publishedAt;

    private boolean deleted;

    @JsonIgnore
    @TimeToLive(unit = TimeUnit.DAYS)
    private int ttl;
}
