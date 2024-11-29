package faang.school.postservice.dto.post;

import faang.school.postservice.dto.comment.CommentNewsFeedDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostNewsFeedDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private Long likes;
    private Long views;
    private List<CommentNewsFeedDto> comments;
    private Boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}