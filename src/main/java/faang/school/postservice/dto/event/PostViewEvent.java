package faang.school.postservice.dto.event;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@ToString
@Builder
public class PostViewEvent implements Serializable {
    private long postId;
    private Long authorId;
    private Long userId;
    private LocalDateTime viewTime;
}
