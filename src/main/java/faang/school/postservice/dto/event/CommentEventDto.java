package faang.school.postservice.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentEventDto {
    private Long postId;
    private Long authorId;
    private Long commentId;
    private LocalDateTime createdAt;
}