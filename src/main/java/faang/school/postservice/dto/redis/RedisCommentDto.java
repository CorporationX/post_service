package faang.school.postservice.dto.redis;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisCommentDto {

    private Long id;
    private Long authorId;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private long amountOfLikes;

    public synchronized void incrementCommentLikes() {
        amountOfLikes++;
    }

    public synchronized void decrementCommentLikes() {
        amountOfLikes--;
    }
}
