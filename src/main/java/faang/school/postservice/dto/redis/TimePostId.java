package faang.school.postservice.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimePostId implements Comparable<TimePostId> {
    
    private long postId;
    private LocalDateTime publishedAt;

    @Override
    public int compareTo(TimePostId o) {
        int value;
        value = publishedAt.compareTo(o.publishedAt);

        if (value != 0) {
            return value;
        }
        value = Long.compare(postId, o.getPostId());
        if (value != 0) {
            return value;
        }
        return 0;
    }
}
