package faang.school.postservice.dto.post;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePostRequest {
    @NotNull(message = "Author ID cannot be null")
    @Min(value = 1)
    private Long authorId;

    @Min(value = 1)
    private Long projectId;

    @NotBlank(message = "Content cannot be empty")
    @Size(max = 4096, message = "Content cannot be longer than 4096 characters")
    private String content;
}
