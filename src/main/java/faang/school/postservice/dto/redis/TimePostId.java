package faang.school.postservice.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimePostId implements Comparable<TimePostId> {

    private long id;
    private LocalDateTime publishedAt;

    @Override
    public int compareTo(TimePostId o) {
        return publishedAt.compareTo(o.publishedAt);
    }
}
