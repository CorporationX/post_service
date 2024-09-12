package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostDto {
    private Long id;

    @NotBlank(message = "The content is empty")
    @Size(min = 1, max = 4096, message = "The content size should be between 1 and 4096 characters")
    String content;

    private Long authorId;
    private Long projectId;
    private boolean published;
    private boolean deleted;
}