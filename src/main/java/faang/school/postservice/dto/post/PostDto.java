package faang.school.postservice.dto.post;

import faang.school.postservice.controller.PostController;
import faang.school.postservice.dto.comment.CommentFeedDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long id;
    @NotNull (groups = PostController.class)
    @NotBlank (message = "Content can not be Blank")
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> commentIds;
    private Long likes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    private boolean published;
    private boolean deleted;
}
