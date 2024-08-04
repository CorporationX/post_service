package faang.school.postservice.dto.Post;

import faang.school.postservice.controller.PostController;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostDto {
    private Long id;
    @NotNull (groups = PostController.class)
    @NotBlank (message = "Content can not be Blank")
    private String content;
    private Long authorId;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean published;
    private boolean deleted;
}
