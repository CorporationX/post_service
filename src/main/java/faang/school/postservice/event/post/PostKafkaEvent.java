package faang.school.postservice.event.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PostKafkaEvent {
    private Long postId;
    private Long authorId;
    private List<Long> followerIds;
    private LocalDateTime createdAt;
}
