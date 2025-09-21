package faang.school.postservice.dto.feed;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FeedPostDto {
    private Long id;
    private String content;
    private Long authorId;
    private FeedUserDto authorUser;
    private Long projectId;
    private List<FeedCommentDto> lastComments;
    private LocalDateTime publishedAt;
}
