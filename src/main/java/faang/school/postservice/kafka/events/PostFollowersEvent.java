package faang.school.postservice.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostFollowersEvent {
    private Long authorId;
    private Long postId;
    private List<Long> followersIds;
    private LocalDateTime publishedAt;
}