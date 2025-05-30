package faang.school.postservice.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class PostViewEvent {
    @JsonProperty("postId")
    private Long postId;
    @JsonProperty("userId")
    private Long userId;
    @JsonProperty("authorId")
    private Long authorId;
    @JsonProperty("date")
    private LocalDateTime date;
}
