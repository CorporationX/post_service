package faang.school.postservice.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedPostDto {
    private Long id;
    private Long authorId;
    private Long projectId;
    private Long likes;
    private LocalDateTime scheduleAt;
    private String authorName;
    private String text;
    private Long likeCount;
}
