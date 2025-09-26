package faang.school.postservice.dto.feed;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedCommentDto {
    private Long id;
    private Long authorId;
    private String content;
    private LocalDateTime createdAt;
}
