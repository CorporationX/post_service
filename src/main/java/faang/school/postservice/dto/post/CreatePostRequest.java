package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePostRequest {

    @NotNull(message = "Author ID cannot be null")
    private Long authorId;

    private Long projectId;

    @NotBlank(message = "Content cannot be empty")
    @Size(max = 4096, message = "Content cannot be longer than 4096 characters")
    private String content;
}
