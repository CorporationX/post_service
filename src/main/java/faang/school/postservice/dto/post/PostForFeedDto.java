package faang.school.postservice.dto.post;

import faang.school.postservice.dto.comment.CommentPublishedEvent;
import faang.school.postservice.dto.user.UserForFeedDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostForFeedDto {
    private long id;
    private String content;
    private UserForFeedDto author;
    private Long projectId;
    private long countOfLikes;
    private long countOfComments;
    private long countOfViews;
    private List<CommentPublishedEvent> comments;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
}
