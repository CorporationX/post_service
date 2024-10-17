package faang.school.postservice.dto.publishable.fornewsfeed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedPostEvent {
    private long postId;
    private long authorId;
    private LocalDateTime publishedAt;
    private List<Long> subscribersIds;
}
