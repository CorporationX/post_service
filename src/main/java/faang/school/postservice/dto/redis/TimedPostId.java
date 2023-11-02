package faang.school.postservice.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimedPostId implements Comparable<TimedPostId> {

    private long postId;
    private LocalDateTime publishedAt;

    @Override
    public int compareTo(TimedPostId o) {
        int value;
        value = publishedAt.compareTo(o.publishedAt);

        if (value != 0) {
            return value;
        }
        value = Long.compare(postId, o.getPostId());

        return value;
    }
}
