package faang.school.postservice.dto.post;

import faang.school.postservice.controller.PostController;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private Long likes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean published;
    private boolean deleted;
}
