package faang.school.postservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewPostEvent {
    private Long postId;
    private Long authorId;
    private Long projectId;
    private String content;
    private LocalDateTime publishedAt;
    private List<Long> followerIds;
}