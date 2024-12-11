package faang.school.postservice.model.event.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostPublishedKafkaEvent {
    private long postId;
    private List<Long> followerIds;
    private LocalDateTime publishedAt;
}
