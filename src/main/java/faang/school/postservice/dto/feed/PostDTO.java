package faang.school.postservice.dto.feed;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostVisibility;
import faang.school.postservice.dto.project.ProjectDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostDTO {
    private Long id;
    private String content;
    private AuthorDTO author;
    private ProjectDto project;
    private Long likesCount;
    private Long commentsCount;
    private List<CommentDto> lastComments;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    private boolean verified;
    private PostVisibility visibility;
}