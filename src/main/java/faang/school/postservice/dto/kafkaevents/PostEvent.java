package faang.school.postservice.dto.kafkaevents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostEvent {
    private Long postId;
    private Long authorId;
    private Instant publishedAt;
    private List<Long> followers;
}
