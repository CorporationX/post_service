package faang.school.postservice.model.event.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class PostEventKafka {
    private long postId;
    private long authorId;
    private List<Long>followerIds;
    LocalDateTime createdAt;
}
