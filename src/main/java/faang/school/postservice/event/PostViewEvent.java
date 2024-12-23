package faang.school.postservice.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class PostViewEvent implements Serializable {
    private Long postId;
    private Long authorId;
    private Long viewerId;
    private LocalDateTime viewedAt;
}