package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreatedKafkaEvent {
    private Long postId;
    private Long authorId;
    private long publishedAtEpochMillis;
    private List<Long> followerIds;
}
